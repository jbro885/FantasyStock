package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.ViewHolder.PeriodChartsView;

import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/21/16.
 */
public class ChartPeriodFragment extends Fragment {
    private PeriodChartsView periodChartsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_period_chart, container, false);
        ButterKnife.bind(this, view);

        periodChartsView = new PeriodChartsView(view, getActivity());
        periodChartsView.setStock(new Stock("portfolios"));

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
        periodChartsView.setStock(new Stock("portfolios"));
    }
}
