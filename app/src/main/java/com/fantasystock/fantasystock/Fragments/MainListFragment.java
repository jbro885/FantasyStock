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
    private ArrayList<Stock> watchlist;
    private ArrayList<News> news;
    private MainListAdapter mainListAdapter;
    private final int EATCHLIST_DISPLAY_SIZE = 4;

    @Bind(R.id.rvList) RecyclerView rvList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        watchlist = new ArrayList<>();
        news = new ArrayList<>();
        items = new ArrayList<>();
        // Todo: Load news and watchlist

        // Todo: Organized
        // Organize data to items

        // Stock watchlist first
        if(watchlist.size() < EATCHLIST_DISPLAY_SIZE) {
            items.addAll(watchlist);
        }
        else {
            for(int i = 0; i < EATCHLIST_DISPLAY_SIZE; i++) {
                items.add(watchlist.get(i));
            }
            // Add Expand all bar
            String str = "EXPAND_ALL";
            items.add(str);
        }
        // News
        items.addAll(news);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        ButterKnife.bind(this, view);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainListAdapter = new MainListAdapter(watchlist, news, rvList);
        mainListAdapter.setOnLoadMoreListener(new MainListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add progress item
                int progress_position = mainListAdapter.getItemCount();
                items.add(null);
                mainListAdapter.notifyItemInserted(progress_position);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        items.remove(items.size() - 1);
                        mainListAdapter.notifyItemRemoved(items.size());
                        //add items one by one
                        // onScrollingDown();
                        mainListAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });
        rvList.setAdapter(mainListAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
