package com.fantasystock.fantasystock.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    ChartsView chartsView;
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
        chartsView = new ChartsView(vChart, fadeBlue);
        chartsView.isDarkTheme = false;
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setStock();

    }


    private void setStock() {
        Stock stock = DataCenter.getInstance().stockMap.get(symbol);
        chartsView.setStock(stock);

        DataClient.getInstance().getStockPrice(symbol, new CallBack() {
            @Override
            public void stockCallBack(Stock stock) {
                tvSymbol.setText(stock.symbol);
                tvName.setText(stock.name);
                tvPrice.setText(stock.current_price + "");
            }
        });

        DataClient.getInstance().getQuoteProfile(symbol, profileCallbackHandler());
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
                tvVolume.setText(profile.vol);
                tvAvgVolume.setText(profile.ave_vol);
            }
        };
    }
}
