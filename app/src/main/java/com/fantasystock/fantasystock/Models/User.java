package com.fantasystock.fantasystock.Models;

import android.telecom.Call;

import com.fantasystock.fantasystock.CallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by chengfu_lin on 3/5/16.
 */

public class User {
    public static User currentUser;
    private final static String USER_WATCH_LIST = "user_watch_list";
    private final static String USER_INVESTING_STOCKS = "user_investing_stocks";
    private final static String USER_AVAILABLE_FUND = "user_available_fund";
    private final static String USER_TOTAL_VALUE = "user_total_value";
    private final static String USER_PROFILE_IMAGE_URL = "user_profile_image_url";

    public String id;
    public String profileImageUrl;
    public String username;
    public double availableFund;
    public double totalValue;
    public HashSet<String> watchlistSet;
    public ArrayList<String> watchlist;

    public ArrayList<Stock> investingStocks;
    public HashMap<String, Stock> investingStocksMap; // this stock keeps the updated shares and avg costs, but the price could be not updated

    public ParseUser user;

    public User(ParseUser user) {
        this.user = user;

        if (user != null) {
            final Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            Gson gson = new Gson();
            watchlist = gson.fromJson(user.getString(USER_WATCH_LIST), listType);
            final Type stockListType = new TypeToken<ArrayList<Stock>>() {}.getType();
            investingStocks = gson.fromJson(user.getString(USER_INVESTING_STOCKS), stockListType);
            availableFund = user.getDouble(USER_AVAILABLE_FUND);
            username = user.getUsername();
            profileImageUrl = user.getString(USER_PROFILE_IMAGE_URL);
            id = user.getObjectId();
        }

        if (watchlistSet==null) watchlistSet = new HashSet<>();
        if (watchlist==null) watchlist = new ArrayList<>();
        if (investingStocks==null) investingStocks = new ArrayList<>();
        if (investingStocksMap==null) investingStocksMap = new HashMap<>();
        if (availableFund == 0) availableFund = 1000000;
        if (totalValue == 0) totalValue = availableFund;

        for (int i=0;i<watchlist.size();++i) {
            String symbol = watchlist.get(i);
            watchlistSet.add(symbol);
        }

        for (int i=0;i<investingStocks.size();++i) {
            Stock stock = investingStocks.get(i);
            investingStocksMap.put(stock.symbol, stock);
        }


    }

    public void updateUser(final CallBack callBack) {
        if (user!=null) {
            Gson gson = new Gson();
            user.put(USER_WATCH_LIST, gson.toJsonTree(watchlist).toString());
            user.put(USER_AVAILABLE_FUND, availableFund);
            user.put(USER_TOTAL_VALUE, totalValue);
            user.put(USER_INVESTING_STOCKS, gson.toJsonTree(investingStocks).toString());
            if (callBack==null) {
                user.saveInBackground();
                return;
            }
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (callBack!=null) {
                        callBack.done();
                    }
                }
            });
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJsonTree(this).toString();
    }

    public static User fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public static void queryUser(String userId, final CallBack callBack) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("_id", userId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    callBack.done(object);
                } else {
                    callBack.onFail(e.toString());
                }
            }
        });
    }


}
