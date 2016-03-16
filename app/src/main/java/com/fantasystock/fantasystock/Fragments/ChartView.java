package com.fantasystock.fantasystock.Fragments;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class ChartView extends RecyclerView.ViewHolder {
    @Bind(R.id.lcChart) LineChart lineChart;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;

    public Drawable fadeBlue;
    private DataClient client;
    private LimitLine openLimitLine;
    private final static String defaultPeriod = "1d";
    public boolean isDarkTheme;
    public Stock stock;
    protected FragmentActivity fragmentActivity;

    public ChartView(View itemView, Drawable fadeBlue, FragmentActivity fragmentActivity) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.fadeBlue = fadeBlue;
        this.fragmentActivity = fragmentActivity;
        client = DataClient.getInstance();
        isDarkTheme = true;
        initChart();
    }


    private void initChart() {

        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        lineChart.setTouchEnabled(true);
//        lineChart.setOnChartGestureListener(this);
//        lineChart.setOnChartValueSelectedListener(this);

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
        lineChart.getAxisLeft().setLabelCount(4, false);
        lineChart.getAxisLeft().setTextColor(isDarkTheme ? Color.WHITE : Color.BLACK);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        PriceMarkView mv = new PriceMarkView(fragmentActivity.getApplicationContext(), R.layout.layout_price_mark_view);

        // set the marker to the chart
        lineChart.setMarkerView(mv);
    }


    public void setStock(Stock stock) {
        this.stock = stock;
        lineChart.getAxisLeft().setTextColor(isDarkTheme?Color.WHITE:Color.BLACK);
        onRefreshPeriod(defaultPeriod);
    }


    private void setData(HistoricalData data) {
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        List<HistoricalData.SeriesEntity> prices = data.series;
        if (prices==null) {
            return;
        }
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
        lineChart.getLegend().setEnabled(false);
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

    protected void onRefreshPeriod(String period) {
        if (stock.symbol.equals("portfolios")) {
            DataCenter.getInstance().portfolios(period, callBackHandler());
        } else {
            client.getHistoricalPrices(stock.symbol, period, callBackHandler());
        }


    }
}
