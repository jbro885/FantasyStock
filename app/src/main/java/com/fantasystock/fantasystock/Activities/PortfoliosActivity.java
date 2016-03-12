package com.fantasystock.fantasystock.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PortfoliosActivity extends AppCompatActivity {
    @Bind(R.id.lcPortfolios) PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolios);
        ButterKnife.bind(this);
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
//        pieChart.setOnChartValueSelectedListener(this);


        DataCenter.getInstance().updateTotalValues(new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> stocks) {
                setData(stocks);
            }
        });


        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

    }

    private void setData(ArrayList<Stock> stocks) {

        int len = stocks.size();
        final DataCenter data = DataCenter.getInstance();
        double total = data.availableFund, change = 0;
        for (int i=0;i<len;++i) {
            Stock stock = stocks.get(i);
            Stock investingStock = data.investingStocksMap.get(stock.symbol);
            total += investingStock.share * stock.current_price;
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i=0;i<len; ++i) {
            Stock stock = stocks.get(i);
            Stock investingStock = data.investingStocksMap.get(stock.symbol);
            yVals1.add(new Entry((float) ((investingStock.share * stock.current_price)/total), i));
            xVals.add(stock.symbol);
        }


        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColor(Color.WHITE);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData pieData = new PieData(xVals, dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
//        pieData.setValueTextColor(Color.WHITE);


//        pieData.setValueTypeface(tf);
        pieChart.setData(pieData);
        int transparentColor = Color.parseColor("#00000000");
        pieChart.setHoleColor(transparentColor);
        pieChart.setBackgroundColor(transparentColor);



        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }
}
