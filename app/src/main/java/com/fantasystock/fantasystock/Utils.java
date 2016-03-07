package com.fantasystock.fantasystock;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wilsonsu on 3/6/16.
 */
public class Utils {
    public static Date timeStampConverter(int timestamp) {
        return new Date((long) timestamp*1000);
    }

    public static String converTimetoRelativeTime(Date time) {
        String relativeDate = DateUtils.getRelativeTimeSpanString(time.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME).toString();
        relativeDate.replaceFirst("hour", "h");
        relativeDate.replaceFirst("minute", "min");
        return relativeDate;
    }
    public static Calendar convertDateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String numberConverter(int n) {
        String text =  (n > 1000 ? n / 1000 + "k" : n + "");
        return text;
    }
}
