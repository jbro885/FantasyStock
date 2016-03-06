package com.fantasystock.fantasystock;

import android.preference.PreferenceActivity;
import android.util.Log;

import com.fantasystock.fantasystock.Models.Stock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

 * // News
 * https://finance.mobile.yahoo.com/v1/newsfeed?cpi=1&lang=en-US&region=US&show_ads=0&q=YHOO
 * http://finance.mobile.yahoo.com/dp/newsfeed?all_content=1&category=userfeed&device_os=2&region=US&lang=en-US
 * http://www.google.com//finance/company_news?output=json&q=
 * */

public class DataClient {
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
    }

    /**
    * // l1: Last Trade(Price Only)   c1: Change  p2: Change Percent  k1:Last Trade (With Time)
    * // a:ask       b:Bid      s:symbol  n:name
    * // RealTime:
    * // b2:ask    b3:Bid c6:Change   k2:Change Percent
    * //quote, price, change, change_percentage, last_trading_time
    * String googleQuoteURL = "http://finance.google.com/finance/info?client=ig&q=";
    * String yahooQuoteURL = "http://finance.yahoo.com/d/quotes.csv?f=sl1c1p2t1n&s=";
    * String ted7726QuoteURL = "http://ted7726finance-wilsonsu.rhcloud.com/?q="
    * */

    public void getStockPrice(String quote, CallBack callback) {
        String url = googleQuoteURL +  quote;
        client.get(url, new RequestParams(), stockHandler(callback));
    }

    private JsonHttpResponseHandler stockHandler(final CallBack callback) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
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
}
