package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Adapters.NewsListAdapter;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class NewsListFragment extends Fragment {
    private ArrayList<News> news;
    private ArrayList<String> previousNews;
    private int lastNewsId;
    private NewsListAdapter newsListAdapter;
    private String symbol;

    @Bind(R.id.rvList) RecyclerView rvList;

    public static NewsListFragment newInstance(String symbol) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            symbol = getArguments().getString("symbol");
        }

        news = new ArrayList<>();
        news.add(null);
        previousNews = new ArrayList<>();
        lastNewsId = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        newsListAdapter = new NewsListAdapter(news, rvList, getActivity());
        rvList.setAdapter(newsListAdapter);
        rvList.getLayoutParams().height = Math.round(DataCenter.getInstance().screenHeight);
        newsListAdapter.setOnLoadMoreListener(new NewsListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add items one by one
                getPreviousNews();
            }
        });

        // Get News
        getLatestNews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void getLatestNews() {
        DataClient.getInstance().getLatestNews(symbol, new CallBack() {
            @Override
            public void latestNewsCallBack(ArrayList<News> latestNews, ArrayList<String> previousNewsId) {
                news.clear();
                news.addAll(latestNews);
                news.add(null);
                previousNews.clear();
                previousNews.addAll(previousNewsId);
                newsListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void getPreviousNews() {
        ArrayList<String> newsId = new ArrayList<>();
        int len = Math.min(previousNews.size(), lastNewsId + 5);
        for (lastNewsId = 0; lastNewsId < len; lastNewsId++) {
            newsId.add(previousNews.get(lastNewsId));
        }
        DataClient.getInstance().getPreviousNewsById(newsId, new CallBack() {
            @Override
            public void previousNewsCallBack(ArrayList<News> previousNews) {
                news.remove(news.size()-1);
                news.addAll(previousNews);
                news.add(null);
                newsListAdapter.notifyDataSetChanged();
                newsListAdapter.setLoaded();
            }
        });
    }
}
