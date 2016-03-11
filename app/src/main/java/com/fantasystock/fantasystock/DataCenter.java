package com.fantasystock.fantasystock;

import android.util.Log;

import com.fantasystock.fantasystock.Models.Stock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

        client = DataClient.getInstance();
        client.getStocksPrice(watchlist, null);
    }
    // shares > 0 -> buy, shares < 0 -> sell
    public void trade(final String symbol, final int shares, final CallBack callBack) {
        // fetch the latest price
        client.getStockPrice(symbol, new CallBack(){
            @Override
            public void stockCallBack(final Stock stock) {
                final float total = stock.current_price * shares;
                Stock investingStock = investingStocksMap.get(symbol);
                if (availableFund >= total || (investingStock != null && investingStock.share + shares >= 0)) {
                    ParseObject transaction = new ParseObject(USER_HISTORY + user.getObjectId());
                    transaction.put(TRANSACTION_SHARES, shares);
                    transaction.put(TRANSACTION_SYMBOL, stock.symbol);
                    transaction.put(TRANSACTION_AVG_PRICE, stock.current_price);
                    transaction.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                availableFund -= total;
                                stock.share += shares;
                                stock.total_cost += total;
                                updateStock(stock);
                                callBack.stockCallBack(stock);
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
        watchlistSet.add(stock.symbol);
        watchlist.add(stock.symbol);
        updateUser();
    }
    public void unfavoriteStock(Stock stock) {
        watchlistSet.remove(stock.symbol);
        for(int i = 0; i < watchlist.size(); i++) {
            if(watchlist.get(i).equals(stock.symbol)) {
                watchlist.remove(i);
                break;
            }
        }
        updateUser();
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

        Log.d("DEBUG", user.toString());
        user.put("cash",1000000);
        // deal something with user
    }

    private void updateUser() {
        if (user!=null) {
            Gson gson = new Gson();
            user.put(USER_WATCH_LIST, gson.toJsonTree(watchlist).toString());
            user.put(USER_AVAILABLE_FUND, availableFund);
            user.put(USER_INVESTING_STOCKS, gson.toJsonTree(investingStocks).toString());
            user.saveInBackground();
        }
    }

    private void updateStock(Stock stock) {
        if (stock.share > 0) {
            if (!investingStocksMap.containsKey(stock.symbol)) {
                investingStocks.add(stock);
            }
            investingStocksMap.put(stock.symbol, stock);
        } else {
            investingStocksMap.remove(stock.symbol);
            investingStocks.remove(stock.symbol);
        }
        stockMap.put(stock.symbol, stock);
        updateUser();
    }
}
