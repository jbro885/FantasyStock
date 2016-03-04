package com.fantasystock.fantasystock.Models;

/**
 * Created by chengfu_lin on 3/3/16.
 */
public class Stock {
    public String name;
    public float price;
    public float quote;
    public float change;
    public float change_percentage;
    public String last_trading_time;
}


/*
From google:
http://finance.google.com/finance/info?client=ig&q=AAPL
[
  {
    "id": "22144",
    "t": "AAPL",
    "e": "NASDAQ",
    "l": "101.50",
    "l_fix": "101.50",
    "l_cur": "101.50",
    "s": "2",
    "ltt": "4:00PM EST",
    "lt": "Mar 3, 4:00PM EST",
    "lt_dts": "2016-03-03T16:00:02Z",
    "c": "+0.75",
    "c_fix": "0.75",
    "cp": "0.74",
    "cp_fix": "0.74",
    "ccol": "chg",
    "pcls_fix": "100.75",
    "el": "101.83",
    "el_fix": "101.83",
    "el_cur": "101.83",
    "elt": "Mar 3, 7:59PM EST",
    "ec": "+0.33",
    "ec_fix": "0.33",
    "ecp": "0.33",
    "ecp_fix": "0.33",
    "eccol": "chg",
    "div": "0.52",
    "yld": "2.05"
  }
]

From weishengsu's website:
http://ted7726finance-wilsonsu.rhcloud.com/?q=AAPL
{
  "data": [
    {
      "quote": "AAPL",
      "price": "101.50",
      "change": "+0.75",
      "chg_pct": "0.74",
      "time": "4:00PM EST"
    }
  ],
  "info": "",
  "status": 200
}
 */