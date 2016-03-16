package com.fantasystock.fantasystock.Activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.fantasystock.fantasystock.Adapters.WatchlistGridViewAdapter;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChartsActivity extends AppCompatActivity {
    private WatchlistGridViewAdapter gridViewAdapter;
    @Bind(R.id.rvGridList)
    RecyclerView rvGridList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        ButterKnife.bind(this);
        ArrayList<String> items = User.currentUser.watchlist;
        gridViewAdapter = new WatchlistGridViewAdapter(this, ContextCompat.getDrawable(this, R.drawable.fade_blue), items);
        rvGridList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvGridList.setAdapter(gridViewAdapter);

//        gridViewAdapter.notifyDataSetChanged();
    }
}
