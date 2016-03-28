package com.fantasystock.fantasystock.Helpers;

import com.fantasystock.fantasystock.Models.HistoricalData;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.Transaction;
import com.fantasystock.fantasystock.Models.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wilsonsu on 3/7/16.
 */
public class DataCenter {
    public static final int REFRESH_WATCHLIST = 200;
    private final static String USER_HISTORY = "user_history";
    private final static String TRANSACTION_SYMBOL = "transaction_symbol";
    private final static String TRANSACTION_SHARES = "transaction_shares";
    private final static String TRANSACTION_AVG_PRICE = "transaction_avg_price";
    public HashMap<String, Stock> stockMap; // these stocks have the updated price, but shares could be wrong
    private DataClient client;
    private User currentUser;
    private String lastViewedStock;
    public float screenHeight;



    private static DataCenter mInstance;
    public static synchronized DataCenter getInstance() {
        if (mInstance == null) {
            mInstance = new DataCenter();
        }
        return mInstance;
    }

    public DataCenter() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        setUser(parseUser);

        client = DataClient.getInstance();
        client.getStocksPrice(currentUser.watchlist, null);

        getTransactions("", new CallBack());
        User.getAllUsersInfos(null, false);
    }
    // shares > 0 -> buy, shares < 0 -> sell
    public void trade(final String symbol, final int shares, final CallBack callBack) {
        // fetch the latest price
        client.getStockPrice(symbol, new CallBack() {
            @Override
            public void stockCallBack(final Stock stock) {
                final float total = stock.current_price * shares;
                Stock iStock = currentUser.investingStocksMap.get(symbol);
                if (iStock == null) iStock = stock;
                final Stock investingStock = iStock;
                if (currentUser.availableFund >= total && investingStock.share + shares >= 0) {
                    ParseObject transaction = new ParseObject(USER_HISTORY + currentUser.id);
                    transaction.put(TRANSACTION_SHARES, shares);
                    transaction.put(TRANSACTION_SYMBOL, stock.symbol);
                    transaction.put(TRANSACTION_AVG_PRICE, stock.current_price);
                    transaction.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                currentUser.availableFund -= total;
                                investingStock.share += shares;
                                investingStock.total_cost += total;
                                updateStock(investingStock, callBack);
                            } else {
                                callBack.onFail("transaction is failed");
                            }
                        }
                    });
                } else {
                    callBack.onFail("available fund is not enough or selling too many shares");
                }
            }
        });
    }

    synchronized public String getLastViewedStock() {
        return lastViewedStock;
    }

    synchronized public void setLastViewedStock(String lastViewedStock) {
        this.lastViewedStock = lastViewedStock;
    }

    public void favoriteStock(Stock stock) {
        if (currentUser.watchlist.contains(stock.symbol)) {
            return;
        }
        currentUser.watchlistSet.add(stock.symbol);
        currentUser.watchlist.add(stock.symbol);
        currentUser.updateUser(null);
    }
    public void unfavoriteStock(Stock stock) {
        if (!currentUser.watchlistSet.contains(stock.symbol)) {
            return;
        }
        currentUser.watchlistSet.remove(stock.symbol);
        for(int i = 0; i < currentUser.watchlist.size(); i++) {
            if(currentUser.watchlist.get(i).equals(stock.symbol)) {
                currentUser.watchlist.remove(i);
                break;
            }
        }
        currentUser.updateUser(null);
    }

    public boolean isFavoritedStock(Stock stock) {
        return currentUser.watchlistSet.contains(stock.symbol);
    }

    public ArrayList<Stock> allFavoritedStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();
        for (int i=0;i<currentUser.watchlist.size();++i) {
            stocks.add(stockMap.get(currentUser.watchlist.get(i)));
        }
        return stocks;
    }

    public void setUser(ParseUser parseUser) {
        User.currentUser = new User(parseUser);
        currentUser = User.currentUser;
        if (stockMap==null) stockMap = new HashMap<>();
        for (int i=0;i<currentUser.investingStocks.size();++i) {
            Stock stock = currentUser.investingStocks.get(i);
            stockMap.put(stock.symbol, stock);
        }
    }

    public void updateTotalValues(final CallBack callBack) {
        DataClient.getInstance().getStocksPrices(currentUser.investingStocks, callBack);
    }

    private void updateStock(Stock stock, CallBack callBack) {
        if (stock==null) {
            callBack.onFail("transaction is failed");
            return;
        }
        if (stock.share > 0) {
            if (!currentUser.investingStocksMap.containsKey(stock.symbol)) {
                currentUser.investingStocks.add(stock);
            }
            favoriteStock(stock);
            currentUser.investingStocksMap.put(stock.symbol, stock);
        } else {
            currentUser.investingStocksMap.remove(stock.symbol);
            currentUser.investingStocks.remove(stock.symbol);
        }
        stockMap.put(stock.symbol, stock);
        currentUser.updateUser(callBack);
    }

    public void portfolios(String period, final CallBack callBack) {

        final int len = currentUser.investingStocks.size();
        final HashSet<String> dataSet = new HashSet<>();
        final ArrayList<HistoricalData> datas = new ArrayList<>();
        for (int i=0;i<len;++i) {
            final String symbol = currentUser.investingStocks.get(i).symbol;
            DataClient.getInstance().getHistoricalPrices(symbol, period, new CallBack(){
                @Override
                public void historicalCallBack(HistoricalData data) {

                    if (!dataSet.contains(symbol)) {
                        dataSet.add(symbol);
                        datas.add(data);
                    }
                    if (dataSet.size() == len) {
                        portfoliosCalculator(datas, callBack);
                    }
                }

                @Override
                public void onFail(String failureMessage) {
                    callBack.onFail(failureMessage);
                }
            });
        }
    }

    private void portfoliosCalculator(ArrayList<HistoricalData> datas, CallBack callBack) {
        int len = datas.size(), seriesLen = Integer.MAX_VALUE;
        for (int i=0;i<len; ++i) {
            seriesLen = Math.min(datas.get(i).series.size(), seriesLen);
        }
        ArrayList<HistoricalData.SeriesEntity> series = new ArrayList<>();
        // this will take care of the case that user hasn't invested any
        if (len==0) {
            seriesLen = 400;
        }
        for (int i=0;i<seriesLen;++i) {

            double close = currentUser.availableFund;
            double open = currentUser.availableFund;
            for (int j=0;j<len; ++j) {
                Stock investingStock = currentUser.investingStocksMap.get(datas.get(j).meta.ticker.toUpperCase());
                if (investingStock!=null) {
                    int share = investingStock.share;
                    close += datas.get(j).series.get(i).close * share;
                    open += datas.get(j).series.get(i).open * share;
                }
            }
            HistoricalData.SeriesEntity seriesEntity = new HistoricalData.SeriesEntity();
            seriesEntity.close = (float)close;
            seriesEntity.open = (float)open;
            series.add(seriesEntity);
        }
        HistoricalData historicalData = new HistoricalData();
        historicalData.series = series;
        callBack.historicalCallBack(historicalData);
    }

    public void getTransactions(String symbol, final CallBack callback) {
        if (currentUser==null) {
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_HISTORY + currentUser.id);
        if (symbol!=null) {
            query.whereEqualTo(TRANSACTION_SYMBOL, symbol);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (scoreList == null) {
                    callback.onFail("scrollList is null");
                    return;
                }
                if (e != null) callback.onFail(e.toString());
                int len = scoreList.size();

                Gson gson = Utils.gsonForParseQuery();
                ArrayList<Transaction> transactions = new ArrayList<>();

                for (int i=0;i<len;++i) {
                    JsonElement json = gson.toJsonTree(scoreList.get(i));
                    Transaction t = gson.fromJson(json.getAsJsonObject().get("state").toString(), Transaction.class);
                    transactions.add(t);
                }
                callback.transactionsCallBack(transactions);
            }
        });
    }
}
