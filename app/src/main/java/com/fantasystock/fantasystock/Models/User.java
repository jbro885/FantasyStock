package com.fantasystock.fantasystock.Models;

import android.text.TextUtils;

import com.fantasystock.fantasystock.Helpers.CallBack;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by chengfu_lin on 3/5/16.
 */

public class User {
    public static User currentUser;
    private final static String USER_WATCH_LIST = "user_watch_list";
    private final static String USER_INVESTING_STOCKS = "user_investing_stocks";
    private final static String USER_AVAILABLE_FUND = "user_available_fund";
    private final static String USER_TOTAL_VALUE = "user_total_value";
    public final static String USER_PROFILE_IMAGE_URL = "user_profile_image_url";

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
            totalValue = user.getDouble(USER_TOTAL_VALUE);
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

        // Assign a random avatar if not exists
        if (TextUtils.isEmpty(profileImageUrl)) {
            this.profileImageUrl = "avatar_" + (int) (Math.random() * 30);
        }

        for (int i=0;i<watchlist.size();++i) {
            String symbol = watchlist.get(i);
            watchlistSet.add(symbol);
        }

        for (int i=0;i<investingStocks.size();++i) {
            Stock stock = investingStocks.get(i);
            if (stock!=null && stock.share>0) {
                investingStocksMap.put(stock.symbol, stock);
            }
        }
    }

    public static boolean isLogin() {
        return ParseUser.getCurrentUser()!=null;
    }

    public void updateUser(final CallBack callBack) {
        if (user!=null) {
            Gson gson = new Gson();
            user.put(USER_WATCH_LIST, gson.toJsonTree(watchlist).toString());
            user.put(USER_AVAILABLE_FUND, availableFund);
            user.put(USER_TOTAL_VALUE, totalValue);
            user.put(USER_INVESTING_STOCKS, gson.toJsonTree(investingStocks).toString());
            user.put(USER_PROFILE_IMAGE_URL, profileImageUrl);
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

    public static void userRank(final CallBack callBack) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> results, ParseException e) {
                if (e == null) {
                    ArrayList<User> users = new ArrayList<User>();
                    int len = results.size();
                    for (int i=0;i<len;++i) {
                        User aUser = new User(results.get(i));
                        users.add(aUser);
                    }
                    Collections.sort(users, new Comparator<User>(){
                        public int compare(User u1, User u2){
                            if(u1.totalValue == u2.totalValue)
                                return 0;
                            return u1.totalValue < u2.totalValue ? 1 : -1;
                        }
                    });
                    callBack.usersCallBack(users);
                } else {
                    callBack.onFail(e.toString());
                }
            }
        });

    }


}
