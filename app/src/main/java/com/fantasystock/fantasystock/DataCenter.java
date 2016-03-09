package com.fantasystock.fantasystock;

import com.fantasystock.fantasystock.Models.Stock;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wilsonsu on 3/7/16.
 */
public class DataCenter {
    public HashMap<String, Stock> favoriteStocks;
    public ArrayList<String> watchlist;
    public HashMap<String, Stock> stockMap;
    public ParseUser user;
    public float availableFund;


    private static DataCenter mInstance;
    public static synchronized DataCenter getInstance() {
        if (mInstance == null) {
            mInstance = new DataCenter();
        }
        return mInstance;
    }

    public DataCenter() {
        favoriteStocks = new HashMap<>();
        watchlist = new ArrayList<>();
        stockMap = new HashMap<>();
        availableFund = 1000000.00f;  // Millionaire!
    }

    public void favoriteStock(Stock stock) {
        favoriteStocks.put(stock.symbol, stock);
        watchlist.add(stock.symbol);
    }
    public void unfavoriteStock(Stock stock) {
        favoriteStocks.remove(stock.symbol);
        for(int i = 0; i < watchlist.size(); i++) {
            if(watchlist.get(i).equals(stock.symbol)) {
                watchlist.remove(i);
                break;
            }
        }
    }
    public boolean isFavoritedStock(Stock stock) {
        return favoriteStocks.containsKey(stock.symbol);
    }

    public ArrayList<Stock> allFavoritedStocks() {
        return new ArrayList<>(favoriteStocks.values());
    }

    public void setUser(ParseUser user) {
        this.user = user;
        // deal something with user
    }
}
