package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.parse.Parse;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private DataClient client;
    private ArrayList<Stock> stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setupParse();
        client = DataClient.getInstance();
//        ParseObject testObject = new ParseObject("TestingFantasyObject");
//        testObject.put("AAPL", "100");
//        testObject.saveInBackground();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        stocks = new ArrayList<>();
        stocks.add(new Stock("AAPL"));
        stocks.add(new Stock("YHOO"));
        stocks.add(new Stock("GOOG"));
        stocks.add(new Stock("FB"));


        client.getStockPrice(stocks, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> returnedSocks) {
                stocks = returnedSocks;
            }
        });
    }

    private void setupParse() {
        Resources res = getResources();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(res.getString(R.string.parse_app_id)) // should correspond to APP_ID env variable
                .clientKey(res.getString(R.string.parse_client_key))  // set explicitly unless clientKey is explicitly configured on Parse server
                .server(res.getString(R.string.parse_server_url)).build());

    }

    @OnClick(R.id.ibSearch)
    public void onSearchClick() {
        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }
}
