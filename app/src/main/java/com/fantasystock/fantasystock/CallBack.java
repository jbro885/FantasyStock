package com.fantasystock.fantasystock;

import com.fantasystock.fantasystock.Models.HistoricalData;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.Models.Stock;

import java.util.ArrayList;

/**
 * Created by wilsonsu on 3/5/16.
 */
public class CallBack {
    public void task() {}
    public void done() {}
    public void stockCallBack(Stock stock) {}
    public void stocksCallBack(ArrayList<Stock> stocks) {}
    public void historicalCallBack(HistoricalData data) {}
    public void profileCallBack(Profile profile) {}
    public void onFail(String failureMessage){}


    // News
    public void latestNewsCallBack(ArrayList<News> latestNews, ArrayList<String> previousNewsId) {}
    public void previousNewsCallBack(ArrayList<News> previousNews) {}
}
