package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fantasystock.fantasystock.Fragments.DetailFragment;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @Bind(R.id.vpViewPager) ViewPager vpViewPager;
    private ArrayList<String> stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");
        ArrayList<String> symbols = intent.getStringArrayListExtra("symbols");


        ButterKnife.bind(this);
        stocks = new ArrayList<>();
        if (symbols==null) {
            if (!User.currentUser.watchlistSet.contains(symbol)) {
                stocks.add(symbol);
            } else {
                stocks = User.currentUser.watchlist;
            }
        } else {
            stocks.addAll(symbols);
        }

        DetailsPagerAdapter detailsPagerAdapter = new DetailsPagerAdapter(getSupportFragmentManager(), stocks, this);
        vpViewPager.setAdapter(detailsPagerAdapter);
        vpViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (stocks.size() > 0) {
                    DataCenter.getInstance().setLastViewedStock(stocks.get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (symbol!=null) {
            setCurrentPageToStock(symbol);
        }
    }


    private void setCurrentPageToStock(String symbol) {
        int len = stocks.size();
        for (int i=0;i<len; ++i) {
            if (symbol.equals(stocks.get(i))) {
                vpViewPager.setCurrentItem(i);
            }
        }
    }

    private static class DetailsPagerAdapter extends FragmentPagerAdapter {
        private FragmentActivity fragmentActivity;
        private ArrayList<String> stocks;
        public DetailsPagerAdapter(FragmentManager fm, ArrayList<String> stocks, FragmentActivity fragmentActivity) {
            super(fm);
            this.fragmentActivity = fragmentActivity;
            if (stocks == null) {
                this.stocks = User.currentUser.watchlist;
            } else {
                this.stocks = stocks;
            }

        }

        @Override
        public Fragment getItem(int position) {
            DetailFragment detailFragment = DetailFragment.newInstance(stocks.get(position));
            detailFragment.fragmentActivity = fragmentActivity;
            return detailFragment;
        }

        @Override
        public int getCount() {
            return stocks.size();
        }



    }
}
