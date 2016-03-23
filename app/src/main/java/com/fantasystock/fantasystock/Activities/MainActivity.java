package com.fantasystock.fantasystock.Activities;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fantasystock.fantasystock.Fragments.ChartPeriodFragment;
import com.fantasystock.fantasystock.Fragments.ChartPieFragment;
import com.fantasystock.fantasystock.Fragments.NewsListFragment;
import com.fantasystock.fantasystock.Fragments.WatchlistChartFragment;
import com.fantasystock.fantasystock.Fragments.WatchlistFragment;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Helpers.FSScrollView;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.ViewHolder.WindowChartView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static int WATCHLIST_TYPE;
    private static final int LIST_MODE = 0;
    private static final int GRID_MODE = 1;

    private static int PROFOLIOS_TYPE;
    private static final int CHART_MODE = 0;
    private static final int PIE_MODE   = 1;

    private ChartPeriodFragment chartPeriodFragment;
    private ChartPieFragment chartPieFragment;
    private WatchlistFragment watchlistFragment;
    private WatchlistChartFragment watchlistChartFragment;
    private NewsListFragment newsListFragment;

    // Profolios section
    @Bind(R.id.tvTotal) TextView tvTotal;
    @Bind(R.id.tvChanges) TextView tvChanges;
    @Bind(R.id.ivProfolioIconChart) ImageView ivProfolioIconChart;
    @Bind(R.id.ivProfolioIconPie) ImageView ivProfolioIconPie;
    @Bind(R.id.flPortfolioChart) FrameLayout flPortfolioChart;

    // Watchlist section
    @Bind(R.id.tvTitleWatchlist) TextView tvTitleWatchlist;
    @Bind(R.id.ivWatchlistIconList) ImageView ivWatchlistIconList;
    @Bind(R.id.ivWatchlistIconGrid) ImageView ivWatchlistIconGrid;
    @Bind(R.id.flWatchListHolder) FrameLayout flWatchListHolder;
    @Bind(R.id.vWatchListHolder) RelativeLayout vWatchListHolder;

    // Floating chart window
    @Bind(R.id.fWindowChart) View fWindowChart;
    @Bind(R.id.rlWindowChart) View windowCharts;
    @Bind(R.id.vTouchView) View vTouchView;
    @Bind(R.id.ibWindowCloseButton) ImageButton ibWindowCloseButton;

    // Background
    @Bind(R.id.ivBackground) ImageView ivBackground;
    @Bind(R.id.ivBackgroundBlurred) ImageView ivBackgroundBlurred;
    @Bind(R.id.rlDim) RelativeLayout rlDim;
    @Bind(R.id.svScrollView) FSScrollView scrollView;

    // toolbar section
    @Bind(R.id.tvIndexName) TextView tvIndexName;
    @Bind(R.id.tvIndexPrice) TextView tvIndexPrice;
    @Bind(R.id.tvIndexPriceChange) TextView tvIndexPriceChange;
    @Bind(R.id.llIndexes) LinearLayout llIndexes;


    // Title bar
    private ArrayList<Stock> stocks;
    private int indexesIndex;

    private WindowChartView windowChartView;
    private Point startPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Set portfolio Section
        PROFOLIOS_TYPE = CHART_MODE;
        chartPeriodFragment = new ChartPeriodFragment();
        chartPieFragment = new ChartPieFragment();
        setProfoliosChart();

        // Set floating window section
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        DataCenter.getInstance().screenHeight = size.y;
        windowChartView = new WindowChartView(fWindowChart, this);
        windowCharts.setAlpha(0.0f);
        ibWindowCloseButton.setAlpha(0.0f);

        vTouchView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Construct draggable shadow for view
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                // Start the drag of the shadow
                view.startDrag(null, shadowBuilder, view, 0);
                // Hide the actual view as shadow is being dragged
                view.setVisibility(View.INVISIBLE);
                return true;
            }

        });
        scrollView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int eventAction = event.getAction();
                if (eventAction == DragEvent.ACTION_DRAG_STARTED) {
                    startPoint = new Point(Math.round(event.getX()), Math.round(event.getY()));
                } else if (eventAction == DragEvent.ACTION_DRAG_ENDED) {
                    vTouchView.post(new Runnable() {
                        @Override
                        public void run() {
                            vTouchView.setVisibility(View.VISIBLE);
                        }
                    });
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(
                            ObjectAnimator.ofFloat(windowCharts, "translationX", windowCharts.getTranslationX(), 0.0f).setDuration(300),
                            ObjectAnimator.ofFloat(windowCharts, "translationY", windowCharts.getTranslationY(), 0.0f).setDuration(300),
                            ObjectAnimator.ofFloat(ibWindowCloseButton, "translationX", windowCharts.getTranslationX(), 0.0f).setDuration(300),
                            ObjectAnimator.ofFloat(ibWindowCloseButton, "translationY", windowCharts.getTranslationY(), 0.0f).setDuration(300)
                    );
                    set.start();
                } else {
                    windowCharts.setTranslationX(event.getX() - startPoint.x);
                    windowCharts.setTranslationY(event.getY() - startPoint.y);
                    ibWindowCloseButton.setTranslationX(event.getX() - startPoint.x);
                    ibWindowCloseButton.setTranslationY(event.getY() - startPoint.y);
                }
                return true;
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                handleScrolling(scrollView.getScrollY());
            }
        });

        // Set Watchlist Section
        WATCHLIST_TYPE = LIST_MODE;
        watchlistFragment = new WatchlistFragment();
        watchlistChartFragment = new WatchlistChartFragment();
        setWatchlist();

        // Set News Section
        newsListFragment = NewsListFragment.newInstance(null, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.flNewsListHolder, newsListFragment).commit();

        // Set Toolbar header Section
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
                        if (investingStock!=null) {
                            total += investingStock.share * stock.current_price;
                            change += investingStock.share * Float.parseFloat(stock.current_change);
                        }
                    }
                    double percentage = change*100/total;
                    tvChanges.setText(Utils.moneyConverter(change) + " ( " + Utils.moneyConverter(percentage) + "% )");
                    if(change < 0) {
                        tvChanges.setTextColor(Color.RED);
                    }
                    else {
                        tvChanges.setTextColor(Color.GREEN);
                    }
                    tvTotal.setText(Utils.moneyConverter(total));
                    User.currentUser.totalValue = total;
                    User.currentUser.updateUser(null);
                }
            });
        }
    }

    @OnClick(R.id.ibMenu)
    public void onMenuClick() {
        startActivity(new Intent(getApplicationContext(), RankActivity.class));
    }

    @OnClick(R.id.ibSearch)
    public void onSearchClick() {
        startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), DataCenter.REFRESH_WATCHLIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DataCenter.REFRESH_WATCHLIST) {
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
        ObjectAnimator closeButtonFadeOut = ObjectAnimator.ofFloat(ibWindowCloseButton, "alpha", 0.0f).setDuration(300);
        windowFadeOut.start();
        closeButtonFadeOut.start();
        DataCenter.getInstance().setLastViewedStock(null);
    }

    @OnClick(R.id.ivWatchlistIconList)
    public void onSetWatchlistViewTypeList() {
        if(WATCHLIST_TYPE == GRID_MODE) {
            WATCHLIST_TYPE = LIST_MODE;
            setWatchlist();
        }
    }

    @OnClick(R.id.ivWatchlistIconGrid)
    public void onSetWatchlistViewTypeGrid() {
        if(WATCHLIST_TYPE == LIST_MODE) {
            WATCHLIST_TYPE = GRID_MODE;
            setWatchlist();
        }
    }

    private void setWatchlist() {
        vWatchListHolder.setMinimumHeight(vWatchListHolder.getHeight());
        CallBack completionCallBack = new CallBack(){
            @Override
            public void task() {
                vWatchListHolder.setMinimumHeight(0);
            }
        };
        if(WATCHLIST_TYPE == LIST_MODE) {
            Utils.fadeInAndOutAnimationGenerator(flWatchListHolder, new CallBack() {
                @Override
                public void task() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flWatchListHolder, watchlistFragment).commit();
                    ivWatchlistIconList.setAlpha((float) 1);
                    ivWatchlistIconGrid.setAlpha((float) 0.5);
                }
            }, completionCallBack);
        }
        else {
            Utils.fadeInAndOutAnimationGenerator(flWatchListHolder, new CallBack() {
                @Override
                public void task() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flWatchListHolder, watchlistChartFragment).commit();
                    ivWatchlistIconList.setAlpha((float) 0.5);
                    ivWatchlistIconGrid.setAlpha((float) 1);
                }
            }, completionCallBack);
        }
    }

    @OnClick(R.id.ivProfolioIconChart)
    public void onSetProfolioViewTypeChart() {
        if(PROFOLIOS_TYPE == PIE_MODE) {
            PROFOLIOS_TYPE = CHART_MODE;
            setProfoliosChart();
        }
    }

    @OnClick(R.id.ivProfolioIconPie)
    public void onSetProfolioViewTypePie() {
        if(PROFOLIOS_TYPE == CHART_MODE) {
            PROFOLIOS_TYPE = PIE_MODE;
            setProfoliosChart();
        }
    }

    private void setProfoliosChart() {
        if(PROFOLIOS_TYPE == CHART_MODE) {
            Utils.fadeInAndOutAnimationGenerator(flPortfolioChart, new CallBack() {
                @Override
                public void task() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flPortfolioChart, chartPeriodFragment).commit();
                    ivProfolioIconChart.setAlpha((float) 1);
                    ivProfolioIconPie.setAlpha((float) 0.5);
                }
            });
        }
        else {
            Utils.fadeInAndOutAnimationGenerator(flPortfolioChart, new CallBack() {
                @Override
                public void task() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flPortfolioChart, chartPieFragment).commit();
                    ivProfolioIconChart.setAlpha((float) 0.5);
                    ivProfolioIconPie.setAlpha((float) 1);
                }
            });
        }
    }
}
