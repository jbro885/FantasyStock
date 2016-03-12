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
    private ArrayList<String> previousNews;
    private int indicator;
    private MainListAdapter mainListAdapter;

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
        news = new ArrayList<>();
        previousNews = new ArrayList<>();
        indicator = 0;
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
        mainListAdapter = new MainListAdapter(items, rvList, getActivity());
        rvList.setAdapter(mainListAdapter);
        mainListAdapter.setOnLoadMoreListener(new MainListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                items.add(null);
                // mainListAdapter.notifyItemInserted(items.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   remove progress item
                        items.remove(items.size() - 1);
                        mainListAdapter.notifyItemRemoved(items.size());
                        //add items one by one
                        getPreviousNews();
                        mainListAdapter.setLoaded();
                    }
                }, 2000);
            }
        });

        // Get Watchlist
        refreshWatchlist();
        // Get News
        getLatestNews();

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
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void refreshWatchlist() {
        mainListAdapter.clear();
        DataClient.getInstance().getStocksPrice(DataCenter.getInstance().watchlist, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                organizeData();
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void getLatestNews() {
        DataClient.getInstance().getLatestNews(new CallBack() {
            @Override
            public void latestNewsCallBack(ArrayList<News> latestNews, ArrayList<String> previousNewsId) {
                news = latestNews;
                previousNews = previousNewsId;
                organizeData();
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void getPreviousNews() {
        ArrayList<String> newsId = new ArrayList<>();
        int indicator_end = indicator + 5;
        for (indicator = 0; indicator < Math.min(previousNews.size(), indicator_end); indicator++) {
            newsId.add(previousNews.get(indicator));
        }
        DataClient.getInstance().getPreviousNewsById(newsId, new CallBack() {
            @Override
            public void previousNewsCallBack(ArrayList<News> previousNews) {
                news.addAll(previousNews);
                organizeData();
                mainListAdapter.notifyDataSetChanged();
                Log.d("DEBUG_PRE", Integer.toString(news.size()));
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

        // News
        title = "NEWS";
        items.add(title);
        items.addAll(news);
    }

}
