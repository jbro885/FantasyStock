package com.fantasystock.fantasystock.ViewHolder;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Models.HistoricalData;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/6/16.
 */
public class ChartView extends RecyclerView.ViewHolder {
    @Bind(R.id.lcChart) LineChart lineChart;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;

    private DataClient client;
    private LimitLine openLimitLine;
    private final static String defaultPeriod = "1d";
    public boolean isDarkTheme;
    public Stock stock;
    protected FragmentActivity fragmentActivity;

    public ChartView(View itemView, FragmentActivity fragmentActivity) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.fragmentActivity = fragmentActivity;
        client = DataClient.getInstance();
        isDarkTheme = true;
        initChart();
    }


    private void initChart() {

        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("");
        lineChart.setNoDataText("");

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


    public void setStock(String symbol) {
        if(symbol.equals("portfolios")) {
            this.stock = new Stock();
            this.stock.symbol = "portfolios";
        }
        else {
            this.stock = DataCenter.getInstance().stockMap.get(symbol);
        }

        lineChart.getAxisLeft().setTextColor(isDarkTheme?Color.WHITE:Color.BLACK);
        onRefreshPeriod(defaultPeriod);
    }


    private void setData(HistoricalData data) {
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<Entry> yValsTradingPoint = new ArrayList<>();
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
            if (i%50==0) {
                yValsTradingPoint.add(new Entry(series.close, i));
            }
        }

        Float changePercentage = 0.0f;
        try {
            changePercentage = Float.parseFloat(stock.current_change_percentage);
        } catch (Exception e) {

        }

        Log.d("DEBUG", stock.current_change_percentage);

        int darkColor;
        Drawable fadeColor;
        if(changePercentage < 0.0f) {
            Log.d("DEBUG1", "RED");
            darkColor = fragmentActivity.getResources().getColor(R.color.red);
            fadeColor = fragmentActivity.getDrawable(R.drawable.fade_red);
        }
        else {
            Log.d("DEBUG1", "BLUE");
            darkColor = fragmentActivity.getResources().getColor(R.color.colorPrimaryGreyDark);
            fadeColor = fragmentActivity.getDrawable(R.drawable.fade_blue);
        }


        LineDataSet lineDataSet= new LineDataSet(yVals, stock.symbol);

        lineDataSet.setColor(darkColor);
        lineDataSet.setCircleColor(darkColor);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(0.0f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillDrawable(fadeColor);
        lineDataSet.setDrawFilled(true);

        LineDataSet lineDataSet2= new LineDataSet(yValsTradingPoint, stock.symbol);

        lineDataSet2.setColor(darkColor, 0);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//        dataSets.add(lineDataSet2);
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

            @Override
            public void onFail(String failureMessage) {
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
