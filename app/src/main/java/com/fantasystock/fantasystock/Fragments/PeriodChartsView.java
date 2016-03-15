package com.fantasystock.fantasystock.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.HistoricalData;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/6/16.
 */
public class PeriodChartsView extends ChartView {
    @Bind(R.id.tvPeriodOneDay) TextView tvPeriodOneDay;
    @Bind(R.id.tvPeriodOneWeek) TextView tvPeriodOneWeek;
    @Bind(R.id.tvPeriodOneMonth) TextView tvPeriodOneMonth;
    @Bind(R.id.tvPeriodAnYear) TextView tvPeriodAnYear;
    @Bind(R.id.tvPeriodHalfYear) TextView tvPeriodHalfYear;
    @Bind(R.id.tvPeriodALL) TextView tvPeriodALL;

    private static final String PERIOD_1D = "1d";
    private static final String PERIOD_1W = "5d";
    private static final String PERIOD_1M = "20d";
    private static final String PERIOD_6M = "6m";
    private static final String PERIOD_1Y = "1y";
    private static final String PERIOD_ALL = "10y";

    public PeriodChartsView(View itemView, Drawable fadeBlue) {
        super(itemView, fadeBlue);
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.tvPeriodALL)
    public void onAllClick() {onPeriodClick(PERIOD_ALL, tvPeriodALL); }
    @OnClick(R.id.tvPeriodAnYear)
    public void onYearClick() {onPeriodClick(PERIOD_1Y, tvPeriodAnYear); }
    @OnClick(R.id.tvPeriodHalfYear)
    public void onHalfYearClick() {onPeriodClick(PERIOD_6M, tvPeriodHalfYear); }
    @OnClick(R.id.tvPeriodOneMonth)
    public void onOneMonthClick() {onPeriodClick(PERIOD_1M, tvPeriodOneMonth); }
    @OnClick(R.id.tvPeriodOneDay)
    public void onOneDayClick() {onPeriodClick(PERIOD_1D, tvPeriodOneDay); }
    @OnClick(R.id.tvPeriodOneWeek)
    public void onOneWeekClick() {onPeriodClick(PERIOD_1W, tvPeriodOneWeek); }

    private void onPeriodClick(String period, TextView textView) {
        super.onRefreshPeriod(period);

        int greyColor = Color.parseColor("#7788AA");
        tvPeriodALL.setTextColor(greyColor);
        tvPeriodAnYear.setTextColor(greyColor);
        tvPeriodHalfYear.setTextColor(greyColor);
        tvPeriodOneDay.setTextColor(greyColor);
        tvPeriodOneWeek.setTextColor(greyColor);
        tvPeriodOneMonth.setTextColor(greyColor);
        textView.setTextColor(isDarkTheme?Color.WHITE:Color.BLACK);
    }
}
