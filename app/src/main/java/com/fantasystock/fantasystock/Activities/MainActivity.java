package com.fantasystock.fantasystock.Activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.fantasystock.fantasystock.CallBack;
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

    @Bind(R.id.flListHolder) FrameLayout flListHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create dummy watchlist
        getWatchlist();

        // Setup parse
        setupParse();
        client = DataClient.getInstance();

//        ParseObject testObject = new ParseObject("TestingFantasyObject");
//        testObject.put("AAPL", "100");
//        testObject.saveInBackground();

        for (int i = 0; i < watchlist.size(); i++) {

        }


        client.getStockPrice("AAPL", new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                stocks = returnedSocks;
            }
        });

        client.getHistoricalPrices("YHOO", "1d", new CallBack() {

        });

        MainListFragment fragment = new MainListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flListHolder, fragment)
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
