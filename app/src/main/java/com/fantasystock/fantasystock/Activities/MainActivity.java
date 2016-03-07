package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Fragments.MainListFragment;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REFRESHWATCHLIST = 200;
    private MainListFragment fragment;
    @Bind(R.id.flMainListHolder) FrameLayout flMainListHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create dummy watchlist
        getWatchlist();

        // get list view
        fragment = new MainListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMainListHolder, fragment)
                .commit();

    }

    private void getWatchlist() {
        DataCenter.getInstance().watchlist.add("MRVL");
        DataCenter.getInstance().watchlist.add("AAPL");
        DataCenter.getInstance().watchlist.add("YHOO");
        DataCenter.getInstance().watchlist.add("MSFT");
        DataCenter.getInstance().watchlist.add("FB");
        DataCenter.getInstance().watchlist.add("GOOGL");
        DataCenter.getInstance().watchlist.add("LNKD");
        DataCenter.getInstance().watchlist.add("TWTR");
    }

    @OnClick(R.id.ibSearch)
    public void onSearchClick() {
        startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), REFRESHWATCHLIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REFRESHWATCHLIST) {
            fragment.refreshWatchlist();
        }
    }
}
