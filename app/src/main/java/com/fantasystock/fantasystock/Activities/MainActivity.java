package com.fantasystock.fantasystock.Activities;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Fragments.NewsListFragment;
import com.fantasystock.fantasystock.Fragments.PeriodChartsView;
import com.fantasystock.fantasystock.Fragments.WatchlistChartFragment;
import com.fantasystock.fantasystock.Fragments.WatchlistFragment;
import com.fantasystock.fantasystock.Fragments.WindowChartView;
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
    private WatchlistChartFragment watchlistChartFragment;
    @Bind(R.id.flWatchListHolder) FrameLayout flWatchListHolder;
    private static int WATCHLIST_TYPE;
    private static final int LIST_MODE = 0;
    private static final int GRID_MODE = 1;

    @Bind(R.id.tvTitleWatchlist) TextView tvTitleWatchlist;

    @Bind(R.id.fCharts) View chartView;
    @Bind(R.id.fWindowChart) View fWindowChart;
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
    @Bind(R.id.svScrollView) ScrollView scrollView;
    @Bind(R.id.ibWindowCloseButton) ImageButton ibWindowCloseButton;

    // Title bar
    private ArrayList<Stock> stocks;
    private int indexesIndex;

    private WindowChartView windowChartView;
    private float windowWidth;
    private Point startPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        PeriodChartsView periodChartsView = new PeriodChartsView(chartView, ContextCompat.getDrawable(this, R.drawable.fade_blue), this);
        periodChartsView.setStock(new Stock("portfolios"));

//        windowChartView.setStock(new Stock("AAPL"));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowWidth = size.x;
        windowChartView = new WindowChartView(fWindowChart, ContextCompat.getDrawable(this, R.drawable.fade_blue), this);
        windowCharts.setAlpha(0.0f);
        ibWindowCloseButton.setAlpha(0.0f);


//        vTouchView.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    // Construct draggable shadow for view
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//                    // Start the drag of the shadow
//                    view.startDrag(null, shadowBuilder, view, 0);
//                    Log.d("DEBUG", "start drag");
//                    // Hide the actual view as shadow is being dragged
//                    view.setVisibility(View.INVISIBLE);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//
//        scrollView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                int eventAction = event.getAction();
//                if (eventAction == DragEvent.ACTION_DRAG_STARTED) {
//                    startPoint = new Point(Math.round(event.getX()), Math.round(event.getY()));
//                } else if (eventAction == DragEvent.ACTION_DRAG_ENDED){
//                    AnimatorSet set = new AnimatorSet();
//                    set.playTogether(
//
//                            ObjectAnimator.ofFloat(windowCharts, "translationX", windowCharts.getTranslationX(), 0.0f).setDuration(300),
//                            ObjectAnimator.ofFloat(windowCharts, "translationY", windowCharts.getTranslationY(), 0.0f).setDuration(300)
//                    );
//                    set.start();
//                    vTouchView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            vTouchView.setVisibility(View.VISIBLE);
//                        }
//                    });
//                } else {
//                    windowCharts.setTranslationX(event.getX() - startPoint.x);
//                    windowCharts.setTranslationY(event.getY() - startPoint.y);
//                }
//                return true;
//            }
//        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                handleScrolling(scrollView.getScrollY());
            }
        });


        // get list view
        WATCHLIST_TYPE = LIST_MODE;
        watchlistFragment = new WatchlistFragment();
        watchlistChartFragment = new WatchlistChartFragment();
        setWatchlist();

        // get news
        newsListFragment = new NewsListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flNewsListHolder, newsListFragment).commit();

        // get header stocks
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
                    User.currentUser.totalValue = total;
                    User.currentUser.updateUser(null);
                }
            });
        }
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
            if(WATCHLIST_TYPE == LIST_MODE) {
                watchlistFragment.refreshWatchlist();
            }
            else {
                watchlistChartFragment.refreshWatchlist();
            }
        }
        String symbol = DataCenter.getInstance().getLastViewedStock();
        if (symbol == null) return;
        windowChartView.setStock(new Stock(symbol));
        windowCharts.setAlpha(1.0f);
        ibWindowCloseButton.setAlpha(1.0f);
    }

    private void handleScrolling(int verticalOffset) {
        float alpha = Math.abs(verticalOffset)/2000.0f;
        if (alpha > 1) {
            alpha = 1;
        }
//        ivBackgroundBlurred.setAlpha(0.6f + alpha * 0.4f);
        ivBackgroundBlurred.setAlpha(alpha);
    }

    @OnClick(R.id.ibWindowCloseButton)
    public void onCloseWindowButton() {
        ObjectAnimator windowFadeOut = ObjectAnimator.ofFloat(windowCharts, "alpha", 0.0f).setDuration(300);
        ObjectAnimator closeButtonFadeOute = ObjectAnimator.ofFloat(ibWindowCloseButton, "alpha", 0.0f).setDuration(300);
        windowFadeOut.start();
        closeButtonFadeOute.start();
    }

    @OnClick(R.id.tvTitleWatchlist)
    public void onSwitchViewType() {
        if(WATCHLIST_TYPE == LIST_MODE) WATCHLIST_TYPE = GRID_MODE;
        else WATCHLIST_TYPE = LIST_MODE;
        setWatchlist();
    }

    private void setWatchlist() {
        Utils.fadeInAndOutAnimationGenerator(flWatchListHolder, new CallBack() {
            @Override
            public void task() {
                flWatchListHolder.setMinimumHeight(flWatchListHolder.getHeight());
                if (WATCHLIST_TYPE == LIST_MODE) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flWatchListHolder, watchlistFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flWatchListHolder, watchlistChartFragment).commit();
                }
            }
        });



    }
}
