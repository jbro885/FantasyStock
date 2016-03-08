package com.fantasystock.fantasystock.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    @Bind(R.id.ablProfileAppBar) AppBarLayout ablProfileAppBar;

    @Bind(R.id.ivBackground) ImageView ivBackground;
    @Bind(R.id.ivBackgroundBlurred) ImageView ivBackgroundBlurred;
    @Bind(R.id.rlDim) RelativeLayout rlDim;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.flMainListHolder, fragment).commit();

        ablProfileAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                handleScrolling(verticalOffset);
            }
        });

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
        DataCenter.getInstance().watchlist.add("INTC");
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

    private void handleScrolling(int verticalOffset) {
        float alpha = Math.abs(verticalOffset)/2500.0f;
        if (alpha > 1) {
            alpha = 1;
        }
        ivBackgroundBlurred.setAlpha(1 - alpha*0.3f);
        Log.d("DEBUG", verticalOffset + ", " + alpha);



    }
}
