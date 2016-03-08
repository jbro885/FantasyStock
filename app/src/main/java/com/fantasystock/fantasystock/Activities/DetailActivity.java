package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fantasystock.fantasystock.Fragments.ChartsFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.github.mikephil.charting.charts.Chart;

public class DetailActivity extends AppCompatActivity {
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Fragment fragment= getSupportFragmentManager().findFragmentById(R.id.fCharts);
        if (fragment instanceof ChartsFragment) {
            ChartsFragment chartsFragment = (ChartsFragment) fragment;
            chartsFragment.setStock(new Stock(symbol));
        }
    }
}
