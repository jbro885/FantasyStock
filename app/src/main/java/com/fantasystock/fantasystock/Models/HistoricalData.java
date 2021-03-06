package com.fantasystock.fantasystock.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wilsonsu on 3/5/16.
 */
public class HistoricalData {
    /**
     * uri : /instrument/1.0/YHOO/chartdata;type=quote;range=1d/json
     * ticker : yhoo
     * Company-Name : Yahoo! Inc.
     * Exchange-Name : NMS
     * unit : MIN
     * timezone : EST
     * currency : USD
     * gmtoffset : -18000
     * previous_close : 32.88
     */

    public MetaEntity meta;

    public Range Timestamp;

    public List<Integer> labels;
    /**
     * timestamp : 1457101918
     * close : 32.85
     * high : 32.865
     * low : 32.79
     * open : 32.79
     * volume : 138800
     */

    public List<SeriesEntity> series;

    public static class MetaEntity {

        public String uri;
        public String ticker;

        @SerializedName("Company-Name")
        public String companyName;
        @SerializedName("Exchange-Name")
        public String exchangeName;
        public String unit;
        public String timezone;
        public String currency;
        public int gmtoffset;
        public float previous_close;
    }

    public static class Range {
        public int min;
        public int max;
    }

    public static class SeriesEntity {
        @SerializedName("Timestamp")
        public int timestamp;
        public float close;
        public float high;
        public float low;
        public float open;
        public int volume;
    }
}
