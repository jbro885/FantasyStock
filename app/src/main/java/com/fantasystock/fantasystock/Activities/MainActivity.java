package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Fragments.ChartsView;
import com.fantasystock.fantasystock.Fragments.MainListFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REFRESH_WATCHLIST = 200;
    private MainListFragment fragment;
    @Bind(R.id.flMainListHolder) FrameLayout flMainListHolder;
    @Bind(R.id.ablProfileAppBar) AppBarLayout ablProfileAppBar;
    @Bind(R.id.fCharts) View chartView;

    @Bind(R.id.ivBackground) ImageView ivBackground;
    @Bind(R.id.ivBackgroundBlurred) ImageView ivBackgroundBlurred;
    @Bind(R.id.rlDim) RelativeLayout rlDim;
    // Indexs
    @Bind(R.id.tvIndexName) TextView tvIndexName;
    @Bind(R.id.tvIndexPrice) TextView tvIndexPrice;
    @Bind(R.id.tvIndexPriceChange) TextView tvIndexPriceChange;
    @Bind(R.id.llIndexes) LinearLayout llIndexes;
    private ArrayList<Stock> stocks;
    private int indexesIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ChartsView chartsView = new ChartsView(chartView, ContextCompat.getDrawable(this, R.drawable.fade_blue));
        chartsView.setStock(new Stock("portfolios"));


        // Create dummy watchlist
        getWatchlist();

        // get list view
        fragment = new MainListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flMainListHolder, fragment).commit();

        ablProfileAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                handleScrolling(verticalOffset);
            }
        });

        stocks = new ArrayList<>();
        stocks.add(new Stock(".DJI"));
        stocks.add(new Stock(".INX"));
        stocks.add(new Stock(".IXIC"));

        DataClient.getInstance().getStocksPrices(stocks, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedStocks) {
                stocks.clear();
                stocks.addAll(returnedStocks);
                stocks.get(0).name = "DOW";
                stocks.get(1).name = "S&P500";
                stocks.get(2).name = "NASDAQ";
                indexesIndex = 0;
                Utils.repeatAnimationGenerator(llIndexes, new CallBack() {
                    @Override
                    public void task() {
                        Stock stock = stocks.get(indexesIndex++);
                        tvIndexName.setText(stock.name);
                        tvIndexPrice.setText(stock.current_price + "");
                        tvIndexPriceChange.setText(stock.current_change + "(" + stock.current_change_percentage + "%)");
                        if (Float.parseFloat(stock.current_change) < 0) {
                            tvIndexPriceChange.setTextColor(Color.RED);
                        } else {
                            tvIndexPriceChange.setTextColor(Color.GREEN);
                        }
                        indexesIndex = indexesIndex % 3;
                    }
                });
            }
        });

    }

    private void getWatchlist() {

    }
    @OnClick(R.id.ibMenu)
    public void onMenuClick() {
        startActivityForResult(new Intent(getApplicationContext(), SignupActivity.class), REFRESH_WATCHLIST);
    }

    @OnClick(R.id.ibSearch)
    public void onSearchClick() {
        startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), REFRESH_WATCHLIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REFRESH_WATCHLIST) {
            fragment.refreshWatchlist();
        }
    }

    private void handleScrolling(int verticalOffset) {
        float alpha = Math.abs(verticalOffset)/3000.0f;
        if (alpha > 1) {
            alpha = 1;
        }
        ivBackgroundBlurred.setAlpha(1 - alpha * 0.3f);
    }
}
