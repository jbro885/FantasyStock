package com.fantasystock.fantasystock;

import com.activeandroid.app.Application;
import com.parse.Parse;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by chengfu_lin on 3/3/16.
 */
public class StockFantasyApplication extends Application {
    private static final String APPLICATION_ID = "FantasyStockID";
    private static final String SERVER_URL = "http://fantasystock.herokuapp.com/parse/";

    @Override
    public void onCreate() {
        super.onCreate();

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID) // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(SERVER_URL).build());
    }
}
