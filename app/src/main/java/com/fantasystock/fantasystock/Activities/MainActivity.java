package com.fantasystock.fantasystock.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Fragments.MainListFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private DataClient client;
    private ArrayList<String> watchlist;
    private ArrayList<Stock> stocks;
    @Bind(R.id.flMainListHolder) FrameLayout flMainListHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create dummy watchlist
        watchlist = new ArrayList<>();
        getWatchlist();

        // get list view
        MainListFragment fragment = MainListFragment.newInstance(watchlist);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMainListHolder, fragment)
                .commit();
    }

    private void getWatchlist() {
        watchlist.add("MRVL");
        watchlist.add("AAPL");
        watchlist.add("YHOO");
        watchlist.add("MSFT");
        watchlist.add("FB");
        watchlist.add("GOOGL");
        watchlist.add("LNKD");
        watchlist.add("TWTR");
    }
}
