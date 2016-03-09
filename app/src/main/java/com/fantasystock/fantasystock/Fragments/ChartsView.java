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
public class ChartsView extends RecyclerView.ViewHolder {
    @Bind(R.id.lcChart) LineChart lineChart;
    @Bind(R.id.tvPeriodOneDay) TextView tvPeriodOneDay;
    @Bind(R.id.tvPeriodOneWeek) TextView tvPeriodOneWeek;
    @Bind(R.id.tvPeriodOneMonth) TextView tvPeriodOneMonth;
    @Bind(R.id.tvPeriodAnYear) TextView tvPeriodAnYear;
    @Bind(R.id.tvPeriodHalfYear) TextView tvPeriodHalfYear;
    @Bind(R.id.tvPeriodALL) TextView tvPeriodALL;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    public Drawable fadeBlue;
    private DataClient client;
    private LimitLine openLimitLine;
    public boolean isDarkTheme;
    public Stock stock;
    private static final String PERIOD_1D = "1d";
    private static final String PERIOD_1W = "5d";
    private static final String PERIOD_1M = "20d";
    private static final String PERIOD_6M = "6m";
    private static final String PERIOD_1Y = "1y";
    private static final String PERIOD_ALL = "10y";

    public ChartsView(View itemView, Drawable fadeBlue) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.fadeBlue = fadeBlue;
        client = DataClient.getInstance();
        isDarkTheme = true;
        Stock stock = new Stock("AAPL");
        setStock(stock);
        initChart();
    }


    private void initChart() {

        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextColor(R.color.grey);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setTextColor(isDarkTheme ? Color.WHITE : Color.BLACK);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
    }


    public void setStock(Stock stock) {
        this.stock = stock;
        lineChart.getAxisLeft().setTextColor(isDarkTheme?Color.WHITE:Color.BLACK);
        onOneDayClick();
    }


    private void setData(HistoricalData data) {
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        List<HistoricalData.SeriesEntity> prices = data.series;
        int len = prices.size();
        if (len>0) {
            lineChart.getAxisLeft().getLimitLines().clear();
            openLimitLine = new LimitLine(prices.get(0).open, "");
            openLimitLine.setLineWidth(1.5f);
            openLimitLine.setLineColor(Color.parseColor("#55ee0000"));
            lineChart.getAxisLeft().addLimitLine(openLimitLine);

        }

        for (int i=0;i<len; ++i) {
            HistoricalData.SeriesEntity series = prices.get(i);
            yVals.add(new Entry(series.close, i));

            xVals.add("");
        }
        int darkColor = Color.parseColor("#2cbcb6");
        LineDataSet lineDataSet= new LineDataSet(yVals, stock.symbol);

        lineDataSet.setColor(darkColor);
        lineDataSet.setCircleColor(darkColor);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(0.0f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setValueTextSize(9f);

        if (fadeBlue!=null) {
            lineDataSet.setFillDrawable(fadeBlue);
        }

        lineDataSet.setDrawFilled(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xVals, dataSets);
        lineChart.setData(lineData);
        lineChart.animateX(1500);
        lineChart.invalidate();
    }

    private CallBack callBackHandler() {
        prLoadingSpinner.setVisibility(View.VISIBLE);
        return new CallBack(){
            @Override
            public void historicalCallBack(HistoricalData returnData) {
                setData(returnData);
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }
        };
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
        client.getHistoricalPrices(stock.symbol, period, callBackHandler());
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
