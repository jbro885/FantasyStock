package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Adapters.WatchlistProfileAdapter;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 4/3/16.
 */
public class WatchlistProfileFragment extends Fragment {
    private List<Object> items;
    private WatchlistProfileAdapter mAdapter;
    private User user;
    private String userId;
    private boolean isDarkTheme;

    // constant
    private final int REFRESH_INTERVAL_MIN = 30;
    private final int REFRESH_INTERVAL_MILLION_SECOND = 60000 * REFRESH_INTERVAL_MIN;

    // For auto refresh
    private Handler handler = new Handler();
    private Runnable autoRefresh = new Runnable(){
        @Override
        public void run() {
            refreshStock();
            handler.postDelayed(autoRefresh, REFRESH_INTERVAL_MILLION_SECOND);
        }
    };

    @Bind(R.id.rvList) RecyclerView rvList;

    public static WatchlistProfileFragment newInstance(String userId, boolean isDarkTheme) {
        WatchlistProfileFragment fragment = new WatchlistProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putBoolean("isDarkTheme", isDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString("userId");
        isDarkTheme = getArguments().getBoolean("isDarkTheme");
        user = User.getUser(userId);


        items = new ArrayList<>();
        // Setup auto refresh
        handler.post(autoRefresh);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new WatchlistProfileAdapter(items, getActivity(), isDarkTheme);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(mAdapter);


        // Get Watchlist
        if (user==null) {
            User.queryUserId(userId, new CallBack() {
                @Override
                public void userCallBack(User user) {
                    setUser(user);
                    refreshWatchlist();
                }
            });
        } else {
            refreshWatchlist();
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void refreshStock() {
        if (user==null) {
            return;
        }
        DataClient.getInstance().getStocksPrice(user.watchlist, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void refreshWatchlist() {
        if (mAdapter!=null) {
            mAdapter.clear();
        }
        if (user != null) {
            DataClient.getInstance().getStocksPrice(user.watchlist, new CallBack() {
                @Override
                public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                    organizeData();
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFail(String failureMessage) {
                    Log.e("ERROR", "getStocksPrice::onFail:" + failureMessage);
                    organizeData();
                    mAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void organizeData() {
        items.clear();
        items.addAll(user.watchlist);
    }

}
