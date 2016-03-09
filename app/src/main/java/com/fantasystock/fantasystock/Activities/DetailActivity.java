package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Fragments.DetailFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    private static class DetailsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Stock> stocks;
        private Drawable fadeBlue;
        public DetailsPagerAdapter(FragmentManager fm, Drawable fadeBlue) {
            super(fm);
            this.fadeBlue = fadeBlue;
            stocks = DataCenter.getInstance().allFavoritedStocks();
        }

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
