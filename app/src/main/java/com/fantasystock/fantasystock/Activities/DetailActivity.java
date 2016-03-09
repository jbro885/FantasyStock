package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
<<<<<<< dd4e839e827c996f7ec7c2c4ef3a97c2695e4cda
import android.graphics.drawable.Drawable;
=======
import android.os.Bundle;
>>>>>>> Add basic trading function
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
<<<<<<< dd4e839e827c996f7ec7c2c4ef3a97c2695e4cda
import android.os.Bundle;
=======
import android.view.View;
import android.widget.TextView;
>>>>>>> Add basic trading function

import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Fragments.DetailFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
<<<<<<< dd4e839e827c996f7ec7c2c4ef3a97c2695e4cda

import java.util.ArrayList;
=======
>>>>>>> Add basic trading function

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {
    @Bind(R.id.vpViewPager) ViewPager vpViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");

        ButterKnife.bind(this);
        Drawable fadeBlue = ContextCompat.getDrawable(this, R.drawable.fade_blue);
        DetailsPagerAdapter detailsPagerAdapter = new DetailsPagerAdapter(getSupportFragmentManager(), fadeBlue);
        vpViewPager.setAdapter(detailsPagerAdapter);

        setCurrentPageToStock(symbol);

    }

    private void setCurrentPageToStock(String symbol) {
        ArrayList<Stock> stocks = DataCenter.getInstance().allFavoritedStocks();
        int len = stocks.size();
        for (int i=0;i<len; ++i) {
            if (symbol.equals(stocks.get(i).symbol)) {
                vpViewPager.setCurrentItem(i);
            }
        }
    }

<<<<<<< dd4e839e827c996f7ec7c2c4ef3a97c2695e4cda
    private static class DetailsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Stock> stocks;
        private Drawable fadeBlue;
        public DetailsPagerAdapter(FragmentManager fm, Drawable fadeBlue) {
            super(fm);
            this.fadeBlue = fadeBlue;
            stocks = DataCenter.getInstance().allFavoritedStocks();
        }
=======
    @OnClick(R.id.btnBuy)
    public void onBuy(View view) {
        onTrade("buy");
    }

    @OnClick(R.id.btnSell)
    public void onSell(View view) {
        onTrade("sell");
    }

    private void onTrade(String buySell) {
        Intent intent = new Intent(this, TradeActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("buySell", buySell);
        startActivity(intent);
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
>>>>>>> Add basic trading function

        @Override
        public Fragment getItem(int position) {
            DetailFragment detailFragment = DetailFragment.newInstance(stocks.get(position).symbol);
            detailFragment.fadeBlue = fadeBlue;
            return detailFragment;
        }

        @Override
        public int getCount() {
            return stocks.size();
        }
    }
}
