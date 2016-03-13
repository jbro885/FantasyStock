package com.fantasystock.fantasystock;

import android.telecom.Call;
import android.util.Log;

import com.fantasystock.fantasystock.Models.HistoricalData;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wilsonsu on 3/7/16.
 */
public class DataCenter {
    private final static String USER_WATCH_LIST = "user_watch_list";
    private final static String USER_INVESTING_STOCKS = "user_investing_stocks";
    private final static String USER_AVAILABLE_FUND = "user_available_fund";
    private final static String USER_HISTORY = "user_history";

    private final static String TRANSACTION_SYMBOL = "transaction_symbol";
    private final static String TRANSACTION_SHARES = "transaction_shares";
    private final static String TRANSACTION_AVG_PRICE = "transaction_avg_price";
    public HashSet<String> watchlistSet;
    public ArrayList<String> watchlist;

    public ArrayList<Stock> investingStocks;
    public HashMap<String, Stock> investingStocksMap; // this stock keeps the updated shares and avg costs, but the price could be not updated

    public HashMap<String, Stock> stockMap; // these stocks have the updated price, but shares could be wrong
    public ParseUser user;
    public double availableFund;
    private DataClient client;


    private static DataCenter mInstance;
    public static synchronized DataCenter getInstance() {
        if (mInstance == null) {
            mInstance = new DataCenter();
        }
        return mInstance;
    }

    public DataCenter() {
        user = ParseUser.getCurrentUser();
        setUser(user);

        client = DataClient.getInstance();
        client.getStocksPrice(watchlist, null);

        getTransactions("", new CallBack());
    }
    // shares > 0 -> buy, shares < 0 -> sell
    public void trade(final String symbol, final int shares, final CallBack callBack) {
        // fetch the latest price
        client.getStockPrice(symbol, new CallBack() {
            @Override
            public void stockCallBack(final Stock stock) {
                final float total = stock.current_price * shares;
                Stock iStock = investingStocksMap.get(symbol);
                if (iStock == null) iStock = stock;
                final Stock investingStock = iStock;
                if (availableFund >= total && investingStock.share + shares >= 0) {
                    ParseObject transaction = new ParseObject(USER_HISTORY + user.getObjectId());
                    transaction.put(TRANSACTION_SHARES, shares);
                    transaction.put(TRANSACTION_SYMBOL, stock.symbol);
                    transaction.put(TRANSACTION_AVG_PRICE, stock.current_price);
                    transaction.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                availableFund -= total;
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

    public void favoriteStock(Stock stock) {
        if (watchlist.contains(stock.symbol)) {
            return;
        }
        watchlistSet.add(stock.symbol);
        watchlist.add(stock.symbol);
        updateUser(null);
    }
    public void unfavoriteStock(Stock stock) {
        if (!watchlistSet.contains(stock.symbol)) {
            return;
        }
        watchlistSet.remove(stock.symbol);
        for(int i = 0; i < watchlist.size(); i++) {
            if(watchlist.get(i).equals(stock.symbol)) {
                watchlist.remove(i);
                break;
            }
        }
        updateUser(null);
    }

    public boolean isFavoritedStock(Stock stock) {
        return watchlistSet.contains(stock.symbol);
    }

    public ArrayList<Stock> allFavoritedStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();
        for (int i=0;i<watchlist.size();++i) {
            stocks.add(stockMap.get(watchlist.get(i)));
        }
        return stocks;
    }

    public void setUser(ParseUser user) {
        this.user = user;
        if (user != null) {
            final Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            Gson gson = new Gson();
            watchlist = gson.fromJson(user.getString(USER_WATCH_LIST), listType);
            final Type stockListType = new TypeToken<ArrayList<Stock>>() {}.getType();
            investingStocks = gson.fromJson(user.getString(USER_INVESTING_STOCKS), stockListType);
            availableFund = user.getDouble(USER_AVAILABLE_FUND);
        }

        if (watchlistSet==null) watchlistSet = new HashSet<>();
        if (watchlist==null) watchlist = new ArrayList<>();
        if (stockMap==null) stockMap = new HashMap<>();
        if (investingStocks==null) investingStocks = new ArrayList<>();
        if (investingStocksMap==null) investingStocksMap = new HashMap<>();
        if (availableFund == 0) availableFund = 1000000;

        for (int i=0;i<watchlist.size();++i) {
            String symbol = watchlist.get(i);
            watchlistSet.add(symbol);
        }

        for (int i=0;i<investingStocks.size();++i) {
            Stock stock = investingStocks.get(i);
            investingStocksMap.put(stock.symbol, stock);
            stockMap.put(stock.symbol, stock);
        }
    }

    private void updateUser(final CallBack callBack) {
        if (user!=null) {
            Gson gson = new Gson();
            user.put(USER_WATCH_LIST, gson.toJsonTree(watchlist).toString());
            user.put(USER_AVAILABLE_FUND, availableFund);
            user.put(USER_INVESTING_STOCKS, gson.toJsonTree(investingStocks).toString());
            if (callBack==null) {
                user.saveInBackground();
                return;
            }
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    callBack.done();
                }
            });
        }
    }

    public void updateTotalValues(final CallBack callBack) {
        DataClient.getInstance().getStocksPrices(investingStocks, callBack);
    }

    private void updateStock(Stock stock, CallBack callBack) {
        if (stock.share > 0) {
            if (!investingStocksMap.containsKey(stock.symbol)) {
                investingStocks.add(stock);
            }
            favoriteStock(stock);
            investingStocksMap.put(stock.symbol, stock);
        } else {
            investingStocksMap.remove(stock.symbol);
            investingStocks.remove(stock.symbol);
        }
        stockMap.put(stock.symbol, stock);
        updateUser(callBack);
    }

    public void portfolios(String period, final CallBack callBack) {

        final int len = investingStocks.size();
        final HashSet<String> dataSet = new HashSet<>();
        final ArrayList<HistoricalData> datas = new ArrayList<>();
        for (int i=0;i<len;++i) {
            final String symbol = investingStocks.get(i).symbol;
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
            });
        }
    }

    private void portfoliosCalculator(ArrayList<HistoricalData> datas, CallBack callBack) {
        int len = datas.size(), seriesLen = Integer.MAX_VALUE;
        for (int i=0;i<len; ++i) {
            seriesLen = Math.min(datas.get(i).series.size(), seriesLen);
        }
        ArrayList<HistoricalData.SeriesEntity> series = new ArrayList<>();
        for (int i=0;i<seriesLen;++i) {

            double close = availableFund;
            double open = availableFund;
            for (int j=0;j<len; ++j) {
                int share = investingStocksMap.get(datas.get(j).meta.ticker.toUpperCase()).share;
                 close += datas.get(j).series.get(i).close * share;
                 open += datas.get(j).series.get(i).open * share;
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
        if (user==null) {
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_HISTORY + user.getObjectId());
        if (symbol!=null) {
            query.whereEqualTo(TRANSACTION_SYMBOL, symbol);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
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
