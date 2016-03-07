package com.fantasystock.fantasystock;

import com.fantasystock.fantasystock.Models.Stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by wilsonsu on 3/7/16.
 */
public class DataCenter {
    private HashMap<String, Stock> favoriteStocks;
    private static DataCenter mInstance;
    public static synchronized DataCenter getInstance() {
        if (mInstance == null) {
            mInstance = new DataCenter();
        }
        return mInstance;
    }

    public DataCenter() {
        favoriteStocks = new HashMap<>();
    }

    public void favoriteStock(Stock stock) {
        favoriteStocks.put(stock.symbol, stock);
    }
    public void unfavoriteStock(Stock stock) {
        favoriteStocks.remove(stock.symbol);
    }
    public boolean isFavoritedStock(Stock stock) {
        return favoriteStocks.containsKey(stock.symbol);
    }

    public ArrayList<Stock> allFavoritedStocks() {
        return new ArrayList<>(favoriteStocks.values());
    }
}
