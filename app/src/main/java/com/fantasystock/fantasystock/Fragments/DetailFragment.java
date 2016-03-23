package com.fantasystock.fantasystock.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.ViewHolder.PeriodChartsView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/8/16.
 */
public class DetailFragment extends Fragment implements TradeFragment.TradeFragmentListener {
    private String symbol;

    @Bind(R.id.tvSymbol)
    TextView tvSymbol;
    @Bind(R.id.tvName) TextView tvName;
    @Bind(R.id.tvPrice) TextView tvPrice;
    @Bind(R.id.tvChanges) TextView tvChanges;
    @Bind(R.id.fDetailCharts) View vChart;
    @Bind(R.id.svScrollView) ScrollView scrollView;

    @Bind(R.id.btnSell) Button btnSell;
    @Bind(R.id.btnBuy) Button btnBuy;

    //Menu
    @Bind(R.id.rlDetailMenu) RelativeLayout rlDetailMenu;
    @Bind(R.id.tvMenuName) TextView tvMenuName;
    @Bind(R.id.tvMenuPrice) TextView tvMenuPrice;
    @Bind(R.id.tvMenuSymbol) TextView tvMenuSymbol;
    @Bind(R.id.llMenuInfo) LinearLayout llMenuInfo;

    private float tvMenuNameStartPoint;

    @Bind(R.id.llSharesInfo) LinearLayout llSharesInfo;
    //Shares Info
    @Bind(R.id.tvShares) TextView tvShares;
    @Bind(R.id.tvEquityValue) TextView tvEquityValue;
    @Bind(R.id.tvTotalReturnPercentage) TextView tvTotalReturnPercentage;
    @Bind(R.id.tvAvgCost) TextView tvAvgCost;
    @Bind(R.id.tvTotalReturn) TextView tvTotalReturn;
    @Bind(R.id.tvTodayReturn) TextView tvTodayReturn;

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

    @Bind(R.id.flTransactions)FrameLayout flTransactions;

    private CommentsFragment commentsFragment;
    private NewsListFragment newsListFragment;
    private BriefTransactionsFragment transactionsFragment;

    PeriodChartsView periodChartsView;
    public FragmentActivity fragmentActivity;

    public static DetailFragment newInstance(String symbol) {
        DetailFragment detailFragment= new DetailFragment ();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        detailFragment.setArguments(args);

        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getArguments().getString("symbol");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, parent, false);
        ButterKnife.bind(this, view);
        periodChartsView = new PeriodChartsView(vChart, fragmentActivity);
        periodChartsView.isDarkTheme = false;
        commentsFragment = BriefCommentsFragment.newInstance(symbol);
        newsListFragment = NewsListFragment.newInstance(symbol, false);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                handleScrolling(scrollView.getScrollY());
            }
        });
        rlDetailMenu.setAlpha(0.0f);
        tvMenuNameStartPoint = tvMenuName.getY();
        tvMenuName.setTranslationY(tvName.getY() - tvMenuName.getY());
        llMenuInfo.setTranslationY(tvName.getY() - tvMenuName.getY());


        transactionsFragment = BriefTransactionsFragment.newInstance(symbol);
        getChildFragmentManager().beginTransaction().replace(R.id.flComments, commentsFragment).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.flNewsListHolder, newsListFragment).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.flTransactions, transactionsFragment).commit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setStock();
    }


    @OnClick(R.id.btnBuy)
    public void onBuy(View view) {
        onTrade(true);
    }

    @OnClick(R.id.btnSell)
    public void onSell(View view) {
        onTrade(false);
    }

    private void onTrade(boolean isBuy) {
        TradeFragment tradeFragment = TradeFragment.newInstance(symbol, isBuy);
        tradeFragment.setTargetFragment(DetailFragment.this, 200);
        tradeFragment.show(getChildFragmentManager(), "200");
    }
    @Override
    public void onFinishTrading() {
        setStock();
    }

    private void setStock() {
        Stock stock = DataCenter.getInstance().stockMap.get(symbol);
        if (stock==null) {
            stock = new Stock(symbol);
        }
        periodChartsView.setStock(stock);


        DataClient.getInstance().getStockPrice(symbol, new CallBack() {
            @Override
            public void stockCallBack(Stock stock) {
                tvSymbol.setText(stock.symbol);
                tvName.setText(stock.name);
                tvPrice.setText(stock.current_price + "");
                tvChanges.setText(stock.current_change + "(" + stock.current_change_percentage + "%)");
                tvMenuName.setText(stock.name);
                tvMenuPrice.setText(stock.current_price + "");
                tvMenuSymbol.setText(stock.symbol);

                if(Float.parseFloat(stock.current_change) < 0 ) {
                    tvPrice.setTextColor(Color.RED);
                    tvChanges.setTextColor(Color.RED);
                }
                else {
                    // check if the fragment is attached to activity to prevent from crash: http://stackoverflow.com/questions/10919240/fragment-myfragment-not-attached-to-activity
                    if (isAdded()) {
                        tvPrice.setTextColor(getResources().getColor(R.color.colorPrimaryGreyDark));
                        tvChanges.setTextColor(getResources().getColor(R.color.colorPrimaryGreyDark));
                    }
                }

                if (!User.currentUser.investingStocksMap.containsKey(symbol) ||
                    User.currentUser.investingStocksMap.get(symbol).share <= 0) {
                    Utils.setHeight(llSharesInfo, 0);
                    Utils.setHeight(flTransactions, 0);
                    btnSell.setVisibility(View.GONE);
                    Utils.setWidth(btnBuy, true);
                } else {
                    Utils.setHeight(llSharesInfo, -1);
                    Utils.setHeight(flTransactions, -1);
                    btnBuy.setWidth(0);
                    btnSell.setVisibility(View.VISIBLE);
                    Utils.setWidth(btnSell, false);
                    Utils.setWidth(btnBuy, false);
                    Stock ownStock = User.currentUser.investingStocksMap.get(symbol);
                    if (ownStock!=null) {
                        tvShares.setText(ownStock.share + "");
                        tvAvgCost.setText(Math.round(ownStock.total_cost / ownStock.share * 100) / 100 + "");
                        tvEquityValue.setText(ownStock.share * stock.current_price + "");
                        tvTodayReturn.setText(Math.round(ownStock.share * Float.parseFloat(stock.current_change)) + "");
                        tvTotalReturn.setText(Math.round(ownStock.share * stock.current_price - ownStock.total_cost) + "");
                        tvTotalReturnPercentage.setText(Utils.moneyConverter((ownStock.share * stock.current_price - ownStock.total_cost) / ownStock.total_cost * 100) + "%");
                    }
                }
            }
        });
        DataClient.getInstance().getQuoteProfile(symbol, profileCallbackHandler());
        commentsFragment.setSymbol(symbol);
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

                try {
                    tvVolume.setText(Utils.numberConverter(Integer.parseInt(profile.vol)));
                } catch (NumberFormatException e ) {
                    tvVolume.setText("N/A");
                }
                try {
                    tvAvgVolume.setText(Utils.numberConverter(Integer.parseInt(profile.ave_vol)));
                } catch (NumberFormatException e ) {
                    tvAvgVolume.setText("N/A");
                }

            }
        };
    }

    private void handleScrolling(int yOffset) {
        float alpha = Math.abs(yOffset)/300.0f;
        if (alpha > 1) {
            alpha = 1;
        }
        rlDetailMenu.setAlpha(alpha);
        
        float translateY = Math.max(tvName.getY()-tvMenuNameStartPoint-yOffset,0.0f);

        tvMenuName.setTranslationY(translateY);
        llMenuInfo.setTranslationY(translateY);

    }


}
