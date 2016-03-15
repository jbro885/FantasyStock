package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Fragments.TransactionsFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.Transaction;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PortfoliosActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    @Bind(R.id.lcPortfolios) PieChart pieChart;
    private ArrayList<Stock> investingStocks;
    private TransactionsFragment transactionsFragment;

    private static final int[] PIE_COLORS = {
            Color.parseColor("#0F00D2"),
            Color.parseColor("#1FE500"),
            Color.parseColor("#FF9200"),
            Color.parseColor("#F9001D"),
            Color.parseColor("#E1007F"),
            Color.parseColor("#8F00CD"),
            Color.parseColor("#3100D0")
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolios);
        ButterKnife.bind(this);
        setupPieChart();
        transactionsFragment = (TransactionsFragment) getSupportFragmentManager().findFragmentById(R.id.fTransactions);
        transactionsFragment.queryTransactions(null);

    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
//        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
//        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);

        investingStocks = User.currentUser.investingStocks;
        DataCenter.getInstance().updateTotalValues(new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> stocks) {
                setData(stocks);
            }
        });

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextColor(Color.WHITE);
    }

    private void setData(ArrayList<Stock> stocks) {

        int len = stocks.size();
        double total = User.currentUser.availableFund, change = 0;
        for (int i=0;i<len;++i) {
            Stock stock = stocks.get(i);
            Stock investingStock = User.currentUser.investingStocksMap.get(stock.symbol);
            total += investingStock.share * stock.current_price;
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i=0;i<len; ++i) {
            Stock stock = stocks.get(i);
            Stock investingStock = User.currentUser.investingStocksMap.get(stock.symbol);
            yVals1.add(new Entry((float) ((investingStock.share * stock.current_price)/total), i));
            xVals.add(stock.symbol);
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColor(Color.WHITE);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : PIE_COLORS)
            colors.add(c);

        dataSet.setColors(colors);
        PieData pieData = new PieData(xVals, dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        int transparentColor = Color.parseColor("#00000000");
        pieChart.setHoleColor(transparentColor);
        pieChart.setBackgroundColor(transparentColor);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        transactionsFragment.queryTransactions(investingStocks.get(e.getXIndex()).symbol);
    }

    @Override
    public void onNothingSelected() {
        transactionsFragment.queryTransactions(null);
    }





}
