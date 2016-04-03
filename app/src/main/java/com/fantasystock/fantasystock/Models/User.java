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
    private final static String USER_FOLLOWINGS = "user_followings";
    public final static String USER_PROFILE_IMAGE_URL = "user_profile_image_url";

    public String id;
    public String profileImageUrl;
    public String profileCoverPhotoUrl;
    public String username;
    public double availableFund;
    public double totalValue;
    public HashSet<String> watchlistSet;
    public ArrayList<String> watchlist;
    public ArrayList<String> followings;
    public ArrayList<Stock> investingStocks;
    public HashMap<String, Stock> investingStocksMap; // this stock keeps the updated shares and avg costs, but the price could be not updated

    public ParseUser user;

    private static HashMap<String, User> userMap = new HashMap<>();

    public User(ParseUser user) {
        this.user = user;

        if (user != null) {
            final Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            Gson gson = new Gson();
            watchlist = gson.fromJson(user.getString(USER_WATCH_LIST), listType);
            followings = gson.fromJson(user.getString(USER_FOLLOWINGS), listType);
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
        if (followings==null) followings= new ArrayList<>();
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

    public void updateUser(final CallBack callBack) {
        if (user!=null) {
            Gson gson = new Gson();
            user.put(USER_WATCH_LIST, gson.toJsonTree(watchlist).toString());
            user.put(USER_AVAILABLE_FUND, availableFund);
            user.put(USER_TOTAL_VALUE, totalValue);
            user.put(USER_INVESTING_STOCKS, gson.toJsonTree(investingStocks).toString());
            user.put(USER_FOLLOWINGS, gson.toJsonTree(followings).toString());
            user.put(USER_PROFILE_IMAGE_URL, profileImageUrl);
            if (callBack==null) {
                user.saveInBackground();
                return;
            }
            userMap.put(id, this);
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

    public void followUser(final String userId, final CallBack callBack) {
        final boolean following = followings.contains(userId);
        if (following) {
            followings.remove(userId);
        } else {
            followings.add(userId);
        }
        updateUser(callBack);
    }

    public void followUser(User user, CallBack callBack) {
        followUser(user.id, callBack);
    }

    /**
     * static methods
     */

    public static boolean isLogin() {
        return ParseUser.getCurrentUser()!=null;
    }


    public static User fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public static void queryUser(String username, final CallBack callBack) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> results, ParseException e) {
                if (e!=null) {
                    callBack.onFail(e.toString());
                    return;
                }
                int len = results.size();
                ArrayList<User> users = new ArrayList<User>();
                for (int i=0;i<len;++i) {
                    User user = new User(results.get(i));
                    users.add(user);
                    userMap.put(user.id, user);
                }
                callBack.usersCallBack(users);
            }
        });
    }

    public static void queryUserId(String userId, final CallBack callBack) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("_id", userId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e!=null) {
                    callBack.onFail(e.toString());
                    return;
                }

                if (object == null || !(object instanceof ParseUser)) {
                    callBack.onFail("not returning a parse user");
                    return;
                }
                ParseUser parseUser = (ParseUser) object;
                final User user = new User(parseUser);
                if (user == null) {
                    return;
                }
                userMap.put(user.id, user);
                callBack.userCallBack(user);
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
                    for (int i = 0; i < len; ++i) {
                        User aUser = new User(results.get(i));
                        users.add(aUser);
                    }
                    Collections.sort(users, new Comparator<User>() {
                        public int compare(User u1, User u2) {
                            if (u1.totalValue == u2.totalValue)
                                return 0;
                            return u1.totalValue < u2.totalValue ? 1 : -1;
                        }
                    });
                    if (callBack != null) {
                        callBack.usersCallBack(users);
                    }

                } else {
                    if (callBack != null) {
                        callBack.onFail(e.toString());
                    }
                }
            }
        });
    }



    public static void getAllUsersInfos(final CallBack callBack, boolean forceReload) {
        if (userMap!=null && !forceReload) {
            return;
        }
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> results, ParseException e) {
                if (e != null) {
                    if (callBack != null) {
                        callBack.onFail(e.toString());
                    }
                    return;
                }
                userMap = new HashMap<String, User>();
                int len = results.size();
                for (int i = 0; i < len; ++i) {
                    User aUser = new User(results.get(i));
                    userMap.put(aUser.id, aUser);
                }
                if (callBack != null) {
                    callBack.done();
                }
            }
        });
    }

    public static User getUser(String userId) {
        if (userId.equals(currentUser.id)) {
            return currentUser;
        }
        return userMap.get(userId);
    }

    public static ArrayList<User> getAllUsers() {
        if (userMap==null) {
            return null;
        }
        ArrayList<User> users = new ArrayList<>(userMap.values());
        Collections.sort(users, new Comparator<User>() {
            public int compare(User u1, User u2) {
                if (u1.totalValue == u2.totalValue)
                    return 0;
                return u1.totalValue < u2.totalValue ? 1 : -1;
            }
        });
        return users;
    }
}
