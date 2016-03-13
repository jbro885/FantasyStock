package com.fantasystock.fantasystock.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class Transaction {
    @SerializedName("objectId")
    public String id;

    @SerializedName("serverData")
    public DataEntity data;
    public Date updatedAt;

    public static class DataEntity {
        @SerializedName("transaction_avg_price")
        public double avgPrice;
        @SerializedName("transaction_shares")
        public int shares;
        @SerializedName("transaction_symbol")
        public String symbol;
    }
}
