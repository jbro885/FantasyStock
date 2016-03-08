package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Fragments.ChartsFragment;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.github.mikephil.charting.charts.Chart;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private String symbol;
    @Bind(R.id.tvSymbol) TextView tvSymbol;
    @Bind(R.id.tvName) TextView tvName;
    @Bind(R.id.tvPrice) TextView tvPrice;

    //Profiles
    @Bind(R.id.tvMarketCap) TextView tvMarketCap;
    @Bind(R.id.tvOpen) TextView tvOpen;
    @Bind(R.id.tvHigh) TextView tvHigh;
    @Bind(R.id.tvLow) TextView tvLow;
    @Bind(R.id.tv52High) TextView tv52High;
    @Bind(R.id.tv52Low) TextView tv52Low;
    @Bind(R.id.tvPERatio) TextView tvPERatio;
    @Bind(R.id.tvDivYield) TextView tvDivYield;
    @Bind(R.id.tvVolume) TextView tvVolume;
    @Bind(R.id.tvAvgVolume) TextView tvAvgVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");
        ButterKnife.bind(this);

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        setStock();

    }

    private void setStock() {
        Stock stock = DataCenter.getInstance().stockMap.get(symbol);
        Fragment fragment= getSupportFragmentManager().findFragmentById(R.id.fCharts);
        if (fragment instanceof ChartsFragment) {
            ChartsFragment chartsFragment = (ChartsFragment) fragment;
            chartsFragment.isDarkTheme = false;
            chartsFragment.setStock(stock);
        }
        tvSymbol.setText(stock.symbol);
        tvName.setText(stock.name);
        tvPrice.setText(stock.current_price + "");
        DataClient.getInstance().getQuoteProfile(symbol, profileCallbackHandler());
    }

    private CallBack profileCallbackHandler() {
        return new CallBack(){
            @Override
            public void profileCallBack(Profile profile) {
                tv52High.setText(profile.yr_high);
                tv52Low.setText(profile.yr_low);
                tvLow.setText(profile.low);
                tvHigh.setText(profile.high);
                tvPERatio.setText(profile.eps);
                tvMarketCap.setText(profile.mkt_cap);
                tvOpen.setText(profile.open);
                tvDivYield.setText(profile.dividend_yld);
                tvVolume.setText(profile.vol);
                tvAvgVolume.setText(profile.ave_vol);
            }
        };
    }

}
