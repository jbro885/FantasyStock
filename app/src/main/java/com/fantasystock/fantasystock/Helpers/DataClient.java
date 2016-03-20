package com.fantasystock.fantasystock.Helpers;

import android.util.Log;

import com.fantasystock.fantasystock.Models.HistoricalData;
import com.fantasystock.fantasystock.Models.Meta;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.Models.Stock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;


/**
 * Created by weishengsu on 3/3/16.
 *
 * String googleQuoteOptionURL = "http://www.google.com/finance/option_chain?type=All&output=json&q=";
 * String yahooQuoteHistoricalData = "http://chartapi.finance.yahoo.com/instrument/1.0/%@/chartdata;type=quote;range=%@/json";
 * String googleQuoteHistoricalData = "http://www.google.com/finance/historical?output=csv&q=GOOG";


 * Profile
 * String yahooQueryAPIProfile = "http://query.yahooapis.com/v1/public/yql?q=select%%20*%%20from%%20yahoo.finance.quotes%%20where%%20symbol%%20in%%20(%%22%@%%22)%%0A%%09%%09&env=http://datatables.org/alltables.env&format=json";
 * //o: Open   g: Day's Low    h: Day’s High	v: Volume   a2: Average Daily Volume
 * //e: Earnings per Share k: 52 Week High	j: 52 week Low  j1: Market Capitalization   y: Dividend Yield

 * String yahooAPIProfile = "http://finance.yahoo.com/d/quotes.csv?f=soghva2ekjj1y&s=";
 * String yahooQuoteAmericanoAPIs = "https://americano-yql.media.yahoo.com/v1/finance?symbols=";
 * String yahooQuoteAPI = "http://finance.yahoo.com/d/quotes.csv?f=l1c1p2sabn&s=";

 * // Search quote
 * String searchYahooQuoteURL = "http://autoc.finance.yahoo.com/autoc?region=US&lang=en&query=";
 * String searchGoogleQuoteURL = "https://www.google.com/finance/match?matchtype=matchall&q=";
 *
 * // Smart link
 * https://mobile-homerun-yql.media.yahoo.com/api/vibe/v1/smartlink?url=https://www.apple.com


 * */

public class DataClient {
    private static HashMap<String, HistoricalData> historicalCache;

    private static final int STATUS_CODE = 200;
    private static String googleQuoteURL = "http://www.google.com/finance/info?infotype=infoquoteall&q=";

    private AsyncHttpClient client;


    private static DataClient mInstance;

    public static synchronized DataClient getInstance() {
        if (mInstance == null) {
            mInstance = new DataClient();
        }
        return mInstance;
    }


    public DataClient() {
        this.client = new AsyncHttpClient();
        historicalCache = new HashMap<>();
    }

    /**
    * // l1: Last Trade(Price Only)   c1: Change  p2: Change Percent  k1:Last Trade (With Time)
    * // a:ask       b:Bid      s:symbol  n:name
    * // RealTime:
    * // b2:ask    b3:Bid c6:Change   k2:Change Percent
    * //quote, price, change, change_percentage, last_trading_time
    * String googleQuoteURL = http://www.google.com/finance/info?infotype=infoquoteall&q=";
    * String yahooQuoteURL = "http://finance.yahoo.com/d/quotes.csv?f=sl1c1p2t1n&s=";
    * String ted7726QuoteURL = "http://ted7726finance-wilsonsu.rhcloud.com/fantasy/quote?q="
    * */

    private static final String ted7726QuoteURL = "http://ted7726finance-wilsonsu.rhcloud.com/fantasy/quote?q=";

    public void getStocksPrices(ArrayList<Stock> stocks, CallBack callback) {
        String quotes = "";
        for (int i=0;i<stocks.size();++i) {
            quotes += stocks.get(i).symbol + ",";
        }

        client.get(ted7726QuoteURL+quotes, new RequestParams(), stocksHandler(callback));
    }

    public void getStocksPrice(ArrayList<String> stocks, CallBack callback) {
        String quotes = "";
        for (int i=0;i<stocks.size();++i) {
            quotes += stocks.get(i) + ",";
        }

        client.get(ted7726QuoteURL+quotes, new RequestParams(), stocksHandler(callback));
    }

    public void getStockPrice(String symbol, final CallBack callBack) {
        client.get(ted7726QuoteURL+symbol, new RequestParams(), stocksHandler(new CallBack(){
            @Override
            public void stocksCallBack(ArrayList<Stock> stocks) {
                if (stocks.size()>0) {
                    callBack.stockCallBack(stocks.get(0));
                } else {
                    callBack.onFail("Return 0 stock");
                }
            }
        }));
    }

    private JsonHttpResponseHandler stocksHandler(final CallBack callback) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Meta meta = generalDataHandler(statusCode, response, callback);
                if (meta!=null) {
                    final Type listType = new TypeToken<ArrayList<Stock>>() {}.getType();
                    Gson gson = new Gson();
                    ArrayList<Stock> stocks = new ArrayList<>();
                    try {
                        stocks = gson.fromJson(meta.data.toString(), listType);
                    } catch (Exception e) {
                        callback.onFail(e.toString());
                        return;
                    }
                    int len = stocks.size();
                    for (int i=0;i<len;++i) {
                        Stock stock = stocks.get(i);
                        DataCenter.getInstance().stockMap.put(stock.symbol, stock);
                    }
                    if (callback!=null) {
                        callback.stocksCallBack(stocks);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "stocksHandler");
                if (statusCode!=STATUS_CODE) {
                    callback.onFail(responseString);
                } else {
                    Gson gson = new Gson();
                    responseString = responseString.substring(3); // This is taking out the "// " in front of the responstString for parsing as Stock
                    final Type listType = new TypeToken<ArrayList<Stock>>() {}.getType();
                    ArrayList<Stock> stocks = gson.fromJson(responseString, listType);
                    callback.stocksCallBack(stocks);
                }
            }
        };
    }


    /**
     * String googleQuoteOptionURL = "http://www.google.com/finance/option_chain?type=All&output=json&q=";
     * String yahooQuoteHistoricalData = "http://chartapi.finance.yahoo.com/instrument/1.0/%@/chartdata;type=quote;range=%@/json";
     * String googleQuoteHistoricalData = "http://www.google.com/finance/historical?output=csv&q=GOOG";
     */
    private static String yahooHistoricalQuoteBaseURL = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    private static String yahooHistoricalQuoteParam = "/chartdata;type=quote;range=";
    private static final String ted7726QuoteHistoricalURL = "http://ted7726finance-wilsonsu.rhcloud.com/fantasy/historical";
    public void getHistoricalPrices(String quote, String period, CallBack callback) {
        RequestParams params = new RequestParams("q", quote);
        params.put("p", period);
        String cacheKey = "historicalCahce" + quote + period;
        if (historicalCache.containsKey(cacheKey)) {
            callback.historicalCallBack(historicalCache.get(cacheKey));
            return;
        }
        client.get(ted7726QuoteHistoricalURL, params, historicalPricesHandler(callback, cacheKey));
    }

    private JsonHttpResponseHandler historicalPricesHandler(final CallBack callback, final String cacheKey) {
        return new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Meta meta = generalDataHandler(statusCode, response, callback);
                if (meta!=null) {
                    Gson gson = new Gson();
                    HistoricalData data = gson.fromJson(meta.data, HistoricalData.class);
                    historicalCache.put(cacheKey, data);
                    callback.historicalCallBack(data);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "historicalPricesHandler");
                if (statusCode!=STATUS_CODE) {
                    callback.onFail(responseString);
                } else {
                    Gson gson = new Gson();
                    responseString = responseString.substring(29, responseString.length() - 1); // This is taking out the finance_charts_json_callback" in front of the responstString for parsing as Stock
                    HistoricalData data = gson.fromJson(responseString, HistoricalData.class);
                    callback.historicalCallBack(data);
                }
            }
        };
    }

    /**
     * * // Search quote
     * String searchYahooQuoteURL = "http://autoc.finance.yahoo.com/autoc?region=US&lang=en&query=";
     * String searchGoogleQuoteURL = "https://www.google.com/finance/match?matchtype=matchall&q=";
     */
    private static String searchGoogleQuoteURL = "https://www.google.com/finance/match?matchtype=matchall&q=";
    public void searchQuote(String query, CallBack callBack) {
        client.get(searchGoogleQuoteURL+query, null, searchQuoteHandler(callBack));
    }
    private JsonHttpResponseHandler searchQuoteHandler(final CallBack callBack) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray matches;
                try {
                    matches = response.getJSONArray("matches");
                } catch (JSONException e) {
                    callBack.onFail(e.toString());
                    return;
                }
                final Type listType = new TypeToken<ArrayList<Stock>>() {}.getType();
                Gson gson = new Gson();
                ArrayList<Stock> stocks = gson.fromJson(matches.toString(), listType);

                callBack.stocksCallBack(dedupStocks(stocks));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "searchQuoteHandler");
                callBack.onFail(errorResponse.toString());
            }
        };
    }

    private ArrayList<Stock> dedupStocks(ArrayList<Stock> stocks) {
        int len = stocks.size();
        ArrayList<Stock> deduplicatedStocks = new ArrayList<>();
        HashSet<String> symbols = new HashSet<>();
        for (int i=0;i<len;++i) {
            Stock stock = stocks.get(i);
            if (!symbols.contains(stock.symbol)) {
                symbols.add(stock.symbol);
                deduplicatedStocks.add(stock);
            }
        }
        return deduplicatedStocks;
    }

    /**
     * // Profile
     *
     */
    private static String ted7726ProfileQuoteURL = "http://ted7726finance-wilsonsu.rhcloud.com/profile?q=";
    public void getQuoteProfile(String symbol, CallBack callBack) {
        client.get(ted7726ProfileQuoteURL+symbol,null, profileQuoteHandler(callBack));
    }
    private JsonHttpResponseHandler profileQuoteHandler(final CallBack callBack) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Meta meta = generalDataHandler(statusCode, response, callBack);
                if (meta != null) {
                    Gson gson = new Gson();
                    final Type listType = new TypeToken<ArrayList<Profile>>() {}.getType();
                    ArrayList<Profile> profiles= gson.fromJson(meta.data, listType);
                    if (profiles.size()>0) {
                        callBack.profileCallBack(profiles.get(0));
                    } else {
                        callBack.onFail(response.toString());
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "profileQuoteHandler");
                callBack.onFail(responseString);
            }
        };
    }



    private Meta generalDataHandler(int statusCode, JSONObject response, CallBack callback) {
        if (statusCode!=STATUS_CODE) {
            callback.onFail(response.toString());
            return null;
        }
        Gson gson = new Gson();
        Meta meta = gson.fromJson(response.toString(), Meta.class);
        if (meta.status!=STATUS_CODE) {
            callback.onFail(response.toString());
            return null;
        }
        return meta;
    }

    /***********************************************************************************************
     *  News
     *
     * https://finance.mobile.yahoo.com/v1/newsfeed?cpi=1&lang=en-US&region=US&show_ads=0&q=YHOO
     * http://finance.mobile.yahoo.com/dp/newsfeed?all_content=1&category=userfeed&device_os=2&region=US&lang=en-US
     * http://www.google.com//finance/company_news?output=json&q=
     **********************************************************************************************/

    private final String YAHOO_NEWS_ALL_URL = "https://finance.mobile.yahoo.com/v1/newsfeed?cpi=1&lang=en-US&region=US&show_ads=0";
    private final String YAHOO_NEWS_ID_URL = "http://finance.mobile.yahoo.com/dp/newsitems?device_os=2&region=US&lang=en-US&uuids=";
    private final String YAHOO_NEWS_SYMBOL_URL = "https://finance.mobile.yahoo.com/v1/newsfeed?cpi=1&lang=en-US&region=US&show_ads=0&category=TICKER%3A";


    public void getLatestNews(String symbol, CallBack callback) {
        RequestParams params = new RequestParams();
        if (symbol!=null) {
            client.get(YAHOO_NEWS_SYMBOL_URL + symbol, params, latestNewsHandler(callback));
        } else {
            client.get(YAHOO_NEWS_ALL_URL, params, latestNewsHandler(callback));
        }
    }

    public void getPreviousNewsById(ArrayList<String> newsId, CallBack callback) {
        //Log.d("ID_DEBUG", YAHOO_NEWS_ALL_URL);
        if(newsId.isEmpty()) return;
        String quotes = "";
        for (int i=0;i<newsId.size();++i) {
            quotes += newsId.get(i) + ",";
        }
        client.get(YAHOO_NEWS_ID_URL + quotes, new RequestParams(), previousNewsHandler(callback));
    }

    public void getLatestNewsBySymbol(String symbol, CallBack callback) {
//        Log.d("zhuqi", YAHOO_NEWS_SYMBOL_URL + symbol);
        client.get(YAHOO_NEWS_SYMBOL_URL + symbol, new RequestParams(), latestNewsHandler(callback));
    }

    private JsonHttpResponseHandler latestNewsHandler(final CallBack callback) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = Utils.gsonCreatorForNewsDateFormater();
                ArrayList<News> latestNews = null;
                ArrayList<String> previousNewsId = new ArrayList<>();

                try {
                    //Log.d("LA_DEBUG", response.toString());
                    JSONObject result = response.getJSONObject("result");
                    JSONArray items = result.getJSONArray("items");
                    latestNews = gson.fromJson(items.toString(), new TypeToken<ArrayList<News>>() {}.getType());
                    // Get more items
                    JSONArray more_items = result.getJSONArray("more_items");
                    for (int i = 0; i < more_items.length(); i++) {
                        previousNewsId.add(more_items.getJSONObject(i).getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.latestNewsCallBack(latestNews, previousNewsId);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "latestNewsHandler");
                callback.onFail(responseString);
            }
        };
    }

    private JsonHttpResponseHandler previousNewsHandler(final CallBack callback) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = Utils.gsonCreatorForNewsDateFormater();
                ArrayList<News> previousNews = null;
                try {
                    //Log.d("PR_DEBUG", response.toString());
                    JSONArray result = response.getJSONObject("items").getJSONArray("result");
                    previousNews = gson.fromJson(result.toString(), new TypeToken<ArrayList<News>>() {}.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.previousNewsCallBack(previousNews);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "previousNewsHandler");
                callback.onFail(responseString);
            }
        };
    }

}
