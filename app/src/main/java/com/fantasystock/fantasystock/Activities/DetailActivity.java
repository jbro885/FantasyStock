package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Adapters.DetailsPagerAdapter;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {
    @Bind(R.id.vpViewPager) ViewPager vpViewPager;
    private ArrayList<String> stocks;
    private DetailsPagerAdapter detailsPagerAdapter;

    @Bind(R.id.fabDollarSign) FloatingActionButton fabDollarSign;
    @Bind(R.id.fabBuy) FloatingActionButton fabBuy;
    @Bind(R.id.fabSell) FloatingActionButton fabSell;
    private Animation fab_open,fab_close, fab_open_disable, fab_close_disable;
    private Animation rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private Boolean hasShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");
        ArrayList<String> symbols = intent.getStringArrayListExtra("symbols");


        ButterKnife.bind(this);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_open_disable = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open_disable);
        fab_close_disable = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close_disable);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

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

        detailsPagerAdapter = new DetailsPagerAdapter(getSupportFragmentManager(), stocks, this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        detailsPagerAdapter.notifyDataSetChanged();
    }

    private void setCurrentPageToStock(String symbol) {
        int len = stocks.size();
        for (int i=0;i<len; ++i) {
            if (symbol.equals(stocks.get(i))) {
                vpViewPager.setCurrentItem(i);
            }
        }
    }

    @OnClick(R.id.fabDollarSign)
    void onClickDollarSign() {
        String symbol = detailsPagerAdapter.getCurrentStockSymbol(vpViewPager);
        if (!User.currentUser.investingStocksMap.containsKey(symbol) ||
                User.currentUser.investingStocksMap.get(symbol).share <= 0) {
            hasShare = false;
        }
        else {
            hasShare = true;
        }

        if(isFabOpen){
            closeFabDollarSign();
        } else {
            openFabDollarSign();
        }
    }

    @OnClick(R.id.fabSell)
    void onClickSellBtn() {
        closeFabDollarSign();
        detailsPagerAdapter.onClickSellBtn(vpViewPager);
    }

    @OnClick(R.id.fabBuy)
    void onClickBuyBtn() {
        closeFabDollarSign();
        detailsPagerAdapter.onClickBuyBtn(vpViewPager);
    }

    private void openFabDollarSign() {
        // open current button
        isFabOpen = true;
        fabDollarSign.startAnimation(rotate_forward);
        // open Buy button
        fabBuy.startAnimation(fab_open);
        fabBuy.setClickable(true);
        // open Sell button
        if(hasShare) {
            fabSell.startAnimation(fab_open);
            fabSell.setClickable(true);
        }
        else {
            fabSell.startAnimation(fab_open_disable);
            fabSell.setClickable(false);
        }
    }

    private void closeFabDollarSign() {
        // close current button
        fabDollarSign.startAnimation(rotate_backward);
        isFabOpen = false;
        // close Buy Button
        fabBuy.startAnimation(fab_close);
        fabBuy.setClickable(false);
        // close Sell button
        if(hasShare) {
            fabSell.startAnimation(fab_close);
        }
        else {
            fabSell.startAnimation(fab_close_disable);
        }
        fabSell.setClickable(false);
    }
}
