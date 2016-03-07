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
import com.fantasystock.fantasystock.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
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
public class ChartsFragment extends Fragment {
    @Bind(R.id.lcChart) LineChart lineChart;
    private DataClient client;
    private HistoricalData data;

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
        lineChart.getAxisLeft().setGridColor(R.color.transparent);


        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);


        client.getHistoricalPrices("YHOO", "1d", callBackHandler());
    }

    public void setData(HistoricalData data) {
        this.data = data;
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        List<HistoricalData.SeriesEntity> prices = data.series;
        int len = prices.size();


        for (int i=0;i<len; ++i) {
            HistoricalData.SeriesEntity series = prices.get(i);
            yVals.add(new Entry(series.close, i));
            xVals.add(i+"a");
        }
        LineDataSet lineDataSet= new LineDataSet(yVals, "dataSet 1");
        lineDataSet.setColor(R.color.darkBlue);
        lineDataSet.setCircleColor(R.color.darkBlue);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setValueTextColor(R.color.grey);
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
