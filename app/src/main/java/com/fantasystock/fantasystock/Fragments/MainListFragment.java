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

import com.fantasystock.fantasystock.Adapters.MainListAdapter;
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class MainListFragment extends Fragment {
    private List<Object> items;
    private ArrayList<News> news;
    private MainListAdapter mainListAdapter;

    // constant
    private final int EATCHLIST_DISPLAY_SIZE = 30;
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
        news = new ArrayList<>();
        items = new ArrayList<>();
        // Todo: Load news and watchlist

        // Setup auto refresh
        handler.post(autoRefresh);
    }

    private void organizeData() {
        // Organize data to items
        // Stock watchlist
        String title = "WATCHLIST";
        items.add(title);
        if(DataCenter.getInstance().watchlist.size() < EATCHLIST_DISPLAY_SIZE) {
            items.addAll(DataCenter.getInstance().watchlist);
        }
        else {
            for(int i = 0; i < EATCHLIST_DISPLAY_SIZE; i++) {
                items.add(DataCenter.getInstance().watchlist.get(i));
            }
            // Add Expand all bar
            title = "EXPAND ALL";
            items.add(title);
        }
        // News
        title = "NEWS";
        items.add(title);
        items.addAll(news);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        ButterKnife.bind(this, view);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainListAdapter = new MainListAdapter(items, rvList);
        rvList.setAdapter(mainListAdapter);

        mainListAdapter.setOnLoadMoreListener(new MainListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                items.add(null);
                mainListAdapter.notifyItemInserted(items.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   remove progress item
                        items.remove(items.size() - 1);
                        mainListAdapter.notifyItemRemoved(items.size());
                        //add items one by one


                        mainListAdapter.setLoaded();
                    }
                }, 2000);
            }
        });

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
                for (int i = 0; i < returnedSocks.size(); i++) {
                    DataCenter.getInstance().stockMap.put(returnedSocks.get(i).symbol, returnedSocks.get(i));
                }
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void refreshWatchlist() {
        mainListAdapter.clear();
        DataClient.getInstance().getStocksPrice(DataCenter.getInstance().watchlist, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                for (int i = 0; i < returnedSocks.size(); i++) {
                    DataCenter.getInstance().stockMap.put(returnedSocks.get(i).symbol, returnedSocks.get(i));
                }
                organizeData();
                mainListAdapter.notifyDataSetChanged();

                for(int i = 0; i < DataCenter.getInstance().watchlist.size(); i++) {
                    Stock s = DataCenter.getInstance().stockMap.get(DataCenter.getInstance().watchlist.get(i));
                    DataCenter.getInstance().favoriteStocks.put(s.symbol, s);
                }
            }
        });
    }


}
