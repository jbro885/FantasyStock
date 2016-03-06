package com.fantasystock.fantasystock.Activities;

import android.content.res.Resources;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.parse.Parse;
import com.parse.ParseObject;

public class MainActivity extends AppCompatActivity {
    private DataClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupParse();
        client = DataClient.getInstance();

//        ParseObject testObject = new ParseObject("TestingFantasyObject");
//        testObject.put("AAPL", "100");
//        testObject.saveInBackground();
        setContentView(R.layout.activity_main);

        client.getStockPrice("AAPL", new CallBack(){
            @Override
            public void stockCallBack(Stock stock) {
                super.stockCallBack(stock);
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
}
