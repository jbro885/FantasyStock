package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Adapters.WatchlistAdapter;
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/11/16.
 */
public class WatchlistFragment extends Fragment {
    private List<Object> items;
    private WatchlistAdapter mAdapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        // Setup auto refresh
        handler.post(autoRefresh);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        mAdapter = new WatchlistAdapter(items, getActivity());
        rvList.setAdapter(mAdapter);

        // Get Watchlist
        refreshWatchlist();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void refreshStock() {
        DataClient.getInstance().getStocksPrice(DataCenter.getInstance().watchlist, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void refreshWatchlist() {
        mAdapter.clear();
        DataClient.getInstance().getStocksPrice(DataCenter.getInstance().watchlist, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                organizeData();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void organizeData() {
        items.clear();
        // Organize data to items
        // Stock watchlist
        String title = "WATCHLIST";
        items.add(title);
        items.addAll(DataCenter.getInstance().watchlist);
    }

}