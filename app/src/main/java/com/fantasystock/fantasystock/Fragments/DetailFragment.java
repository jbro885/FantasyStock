package com.fantasystock.fantasystock.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.fantasystock.fantasystock.Activities.TradeActivity;
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/8/16.
 */
public class DetailFragment extends Fragment{
    private String symbol;

    @Bind(R.id.tvSymbol)
    TextView tvSymbol;
    @Bind(R.id.tvName) TextView tvName;
    @Bind(R.id.tvPrice) TextView tvPrice;
    @Bind(R.id.fDetailCharts) View vChart;


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

    private CommentsFragment commentsFragment;
    private NewsListFragment newsListFragment;

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
        commentsFragment = CommentsFragment.newInstance(symbol);
        newsListFragment = new NewsListFragment();

        getChildFragmentManager().beginTransaction().replace(R.id.flComments, commentsFragment).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.flNewsListHolder, newsListFragment).commit();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setStock();
    }


    @OnClick(R.id.btnBuy)
    public void onBuy(View view) {
        onTrade("buy");
    }

    @OnClick(R.id.btnSell)
    public void onSell(View view) {
        onTrade("sell");
    }

    private void onTrade(String buySell) {
        Intent intent = new Intent(getContext(), TradeActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("buySell", buySell);
        startActivity(intent);
    }

    private void setStock() {
        Stock stock = DataCenter.getInstance().stockMap.get(symbol);
        periodChartsView.setStock(stock);


        DataClient.getInstance().getStockPrice(symbol, new CallBack() {
            @Override
            public void stockCallBack(Stock stock) {
                tvSymbol.setText(stock.symbol);
                tvName.setText(stock.name);
                tvPrice.setText(stock.current_price + "");
                if (!User.currentUser.investingStocksMap.containsKey(symbol)) {
                    Utils.setHeight(llSharesInfo, 0);
                } else {
                    Utils.setHeight(llSharesInfo, -1);
                    Stock ownStock = User.currentUser.investingStocksMap.get(symbol);
                    tvShares.setText(ownStock.share + "");
                    tvAvgCost.setText(Math.round(ownStock.total_cost / ownStock.share * 100) / 100 + "");
                    tvEquityValue.setText(ownStock.share * stock.current_price + "");
                    tvTodayReturn.setText(Math.round(ownStock.share * Float.parseFloat(stock.current_change)) + "");
                    tvTotalReturn.setText(Math.round(ownStock.share * stock.current_price - ownStock.total_cost) + "");
                    tvTotalReturnPercentage.setText(Utils.moneyConverter((ownStock.share * stock.current_price - ownStock.total_cost) / ownStock.total_cost * 100) + "%");
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
}
