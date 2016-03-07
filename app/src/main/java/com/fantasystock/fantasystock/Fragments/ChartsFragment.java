package com.fantasystock.fantasystock.Fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by wilsonsu on 3/6/16.
 */
public class ChartsFragment extends Fragment {
    @Bind(R.id.lcChart) LineChart lineChart;
    private DataClient client;
    private HistoricalData data;
    private LimitLine openLimitLine;
    public Stock stock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = DataClient.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, parent, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getActivity());

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
        lineChart.getAxisLeft().setTextColor(R.color.grey);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);






        client.getHistoricalPrices("YHOO", "1d", callBackHandler());
    }
    public void setStock(Stock stock) {
        this.stock = stock;

    }

    private void setData(HistoricalData data) {
        this.data = data;
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        List<HistoricalData.SeriesEntity> prices = data.series;
        int len = prices.size();
        if (len>0) {
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
        LineDataSet lineDataSet= new LineDataSet(yVals, "YHOO");

        lineDataSet.setColor(darkColor);
        lineDataSet.setCircleColor(darkColor);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(0.0f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setValueTextSize(9f);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_blue);
        lineDataSet.setFillDrawable(drawable);
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
        return new CallBack(){
            @Override
            public void historicalCallBack(HistoricalData returnData) {
                setData(returnData);
            }
        };
    }


}
