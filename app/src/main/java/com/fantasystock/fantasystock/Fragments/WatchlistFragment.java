package com.fantasystock.fantasystock.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Adapters.WatchlistAdapter;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Helpers.ListItemTouchHelperCallback;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/11/16.
 */
public class WatchlistFragment extends Fragment implements WatchlistAdapter.OnStartDragListener{
    private List<Object> items;
    private WatchlistAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
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

    public static WatchlistFragment newInstance(String userId, boolean isDarkTheme) {
        WatchlistFragment fragment = new WatchlistFragment();
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

        mAdapter = new WatchlistAdapter(items, getActivity(), isDarkTheme);
        mAdapter.setOnStartDragListener(this);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ListItemTouchHelperCallback(mAdapter){
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                View itemView= viewHolder.itemView;
                scaleItem(itemView, false);
                super.clearView(recyclerView, viewHolder);
            }
        };
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvList);

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
        DataClient.getInstance().getStocksPrice(user.watchlist, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    public void refreshWatchlist() {
        mAdapter.clear();
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

        View itemView= viewHolder.itemView;
        scaleItem(itemView, true);
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void scaleItem(View itemView, boolean scaleUp) {
        AnimatorSet set = new AnimatorSet();
        float from = scaleUp?1.00f:1.03f;
        float to = scaleUp?1.03f:1.00f;
        set.playTogether(ObjectAnimator.ofFloat(itemView, "scaleX", from, to).setDuration(300),
                        ObjectAnimator.ofFloat(itemView, "scaleY", from, to).setDuration(300) );
        set.start();
    }
}