package com.fantasystock.fantasystock.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/21/16.
 */
public class ChartPieFragment extends Fragment implements OnChartValueSelectedListener {
    @Bind(R.id.lcPortfolios) PieChart pieChart;
    private ArrayList<Stock> investingStocks;
    private User user;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        ButterKnife.bind(this, view);

        setupPieChart();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getActivity());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPieChart();
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
//        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
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

        setUser(getUser());

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextColor(Color.WHITE);
    }

    public void setUser(User user) {
        this.user = user;

        investingStocks = user.investingStocks;
        DataCenter.getInstance().updateTotalValues(new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> stocks) {
                setData(stocks);
            }
        });
    }

    private User getUser(){
        User user;
        if (this.user==null) {
            user = User.currentUser;
        } else {
            user = this.user;
        }
        return user;
    }

    private void setData(ArrayList<Stock> stocks) {
        User user = getUser();
        int len = stocks.size();
        double total = user.availableFund, change = 0;
        for (int i=0;i<len;++i) {
            Stock stock = stocks.get(i);
            Stock investingStock = user.investingStocksMap.get(stock.symbol);
            if (investingStock!=null) {
                total += investingStock.share * stock.current_price;
            }
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i=0;i<len; ++i) {
            Stock stock = stocks.get(i);
            Stock investingStock = user.investingStocksMap.get(stock.symbol);
            yVals1.add(new Entry((float) ((investingStock.share * stock.current_price)/total), i));
            xVals.add(stock.symbol);
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColor(Color.WHITE);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : PIE_COLORS)
            colors.add(c);

        dataSet.setColors(colors);
        PieData pieData = new PieData(xVals, dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        int transparentColor = Color.parseColor("#00000000");

        pieChart.setCenterText("$"+ Utils.moneyConverter(user.availableFund));
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setHoleColor(transparentColor);
        pieChart.setBackgroundColor(transparentColor);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        String symbol = investingStocks.get(e.getXIndex()).symbol;
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("symbol", symbol);
        getActivity().startActivityForResult(intent, DataCenter.REFRESH_WATCHLIST);
    }

    @Override
    public void onNothingSelected() {
        // Do nothing
    }
}
