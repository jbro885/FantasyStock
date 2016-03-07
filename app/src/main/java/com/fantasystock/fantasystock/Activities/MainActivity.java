package com.fantasystock.fantasystock.Activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Fragments.MainListFragment;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.parse.Parse;

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

        // Setup parse
        setupParse();

        // get list view
        MainListFragment fragment = MainListFragment.newInstance(watchlist);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMainListHolder, fragment)
                .commit();

    }

    private void setupParse() {
        Resources res = getResources();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(res.getString(R.string.parse_app_id)) // should correspond to APP_ID env variable
                .clientKey(res.getString(R.string.parse_client_key))  // set explicitly unless clientKey is explicitly configured on Parse server
                .server(res.getString(R.string.parse_server_url)).build());

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
