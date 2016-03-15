package com.fantasystock.fantasystock.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    @Bind(R.id.pgTabs) PagerSlidingTabStrip pgSlidingTab;
    @Bind(R.id.vpInfoViewPager) ViewPager vpInfoViewPager;
    PeriodChartsView periodChartsView;
    public Drawable fadeBlue;

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
        periodChartsView = new PeriodChartsView(vChart, fadeBlue);
        periodChartsView.isDarkTheme = false;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        InfoPagerAdapter detailsPagerAdapter = new InfoPagerAdapter(getChildFragmentManager(), symbol);
        vpInfoViewPager.setAdapter(detailsPagerAdapter);
        pgSlidingTab.setViewPager(vpInfoViewPager);
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


    }



    private static class InfoPagerAdapter extends FragmentPagerAdapter {
        private String symbol;
        public InfoPagerAdapter(FragmentManager fm, String symbol) {
            super(fm);
            this.symbol = symbol;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return DetailNewsListFragment.newInstance(symbol);
            }
            else if (position == 1 ) {
                return ProfileFragment.newInstance(symbol);
            }
            return CommentsFragment.newInstance(symbol);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "NEWS";
            } else if (position ==1) {
                return "PROFILE";
            }
            return "COMMENTS";
        }
    }
}
