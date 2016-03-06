package com.fantasystock.fantasystock;

import com.fantasystock.fantasystock.Models.Stock;

import java.util.ArrayList;

/**
 * Created by wilsonsu on 3/5/16.
 */
public class CallBack {
    public void task() {}
    public void stockCallBack(Stock stock) {}
    public void stocksCallBack(ArrayList<Stock> stocks) {}
    public void onFail(String failureMessage){}

}
