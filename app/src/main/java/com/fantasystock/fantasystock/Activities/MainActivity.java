package com.fantasystock.fantasystock.Activities;


import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Fragments.ChartView;
import com.fantasystock.fantasystock.Fragments.PeriodChartsView;
import com.fantasystock.fantasystock.Fragments.NewsListFragment;
import com.fantasystock.fantasystock.Fragments.WatchlistFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REFRESH_WATCHLIST = 200;
    private NewsListFragment newsListFragment;
    private WatchlistFragment watchlistFragment;
    @Bind(R.id.fCharts) View chartView;
    @Bind(R.id.rlWindowChart) View windowCharts;

    @Bind(R.id.ivBackground) ImageView ivBackground;
    @Bind(R.id.ivBackgroundBlurred) ImageView ivBackgroundBlurred;
    @Bind(R.id.rlDim) RelativeLayout rlDim;
    // Indexs
    @Bind(R.id.tvIndexName) TextView tvIndexName;
    @Bind(R.id.tvIndexPrice) TextView tvIndexPrice;
    @Bind(R.id.tvIndexPriceChange) TextView tvIndexPriceChange;
    @Bind(R.id.tvTotal) TextView tvTotal;
    @Bind(R.id.tvChanges) TextView tvChanges;
    @Bind(R.id.llIndexes) LinearLayout llIndexes;
    @Bind(R.id.vTouchView) View vTouchView;
    // Title bar
    private ArrayList<Stock> stocks;
    private int indexesIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        PeriodChartsView periodChartsView = new PeriodChartsView(chartView, ContextCompat.getDrawable(this, R.drawable.fade_blue));
        periodChartsView.setStock(new Stock("portfolios"));

        ChartView chartView = new ChartView(windowCharts, ContextCompat.getDrawable(this, R.drawable.fade_blue));
        chartView.setStock(new Stock("AAPL"));

//        vTouchView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("DEBUG", event.getY() + "," + event.getX());
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                    ClipData data = ClipData.newPlainText("", "");
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
//                    v.startDrag(data, shadowBuilder, v, 0);
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        });
//
//        vTouchView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                Log.d("DEBUG", "Dragging!!!:"+event.getY() + "," + event.getX());
//                return false;
//            }
//        });


        // Create dummy watchlist
        getWatchlist();

        // get list view
        watchlistFragment = new WatchlistFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flWatchListHolder, watchlistFragment).commit();
        newsListFragment = new NewsListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flNewsListHolder, newsListFragment).commit();

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
        if (User.currentUser != null) {
            DataCenter.getInstance().updateTotalValues(new CallBack() {
                @Override
                public void stocksCallBack(ArrayList<Stock> stocks) {
                    int len = stocks.size();
                    double total = User.currentUser.availableFund, change = 0;
                    for (int i = 0; i < len; ++i) {
                        Stock stock = stocks.get(i);
                        Stock investingStock = User.currentUser.investingStocksMap.get(stock.symbol);
                        total += investingStock.share * stock.current_price;
                        change += investingStock.share * Float.parseFloat(stock.current_change);
                    }
                    tvChanges.setText(Utils.moneyConverter(change) + " ( " + Utils.moneyConverter(change / total * 100) + "% )");
                    tvTotal.setText(Utils.moneyConverter(total));
                }
            });
        }

    }

    private void getWatchlist() {

    }
    @OnClick(R.id.ibMenu)
    public void onMenuClick() {
        if (User.currentUser!=null && User.currentUser.username!=null) {
            startActivityForResult(new Intent(getApplicationContext(), PortfoliosActivity.class), REFRESH_WATCHLIST);
        } else {
            startActivityForResult(new Intent(getApplicationContext(), SignupActivity.class), REFRESH_WATCHLIST);
        }
    }

    @OnClick(R.id.ibSearch)
    public void onSearchClick() {
        startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), REFRESH_WATCHLIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REFRESH_WATCHLIST) {
            watchlistFragment.refreshWatchlist();
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
