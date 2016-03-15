package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Profile;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/14/16.
 */
public class ProfileFragment extends Fragment{
    private String symbol;
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

    public static ProfileFragment newInstance(String symbol) {
        ProfileFragment detailFragment= new ProfileFragment ();
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
        View view = inflater.inflate(R.layout.fragment_profile, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
