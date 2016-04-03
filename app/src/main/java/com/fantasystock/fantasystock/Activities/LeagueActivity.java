package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.fantasystock.fantasystock.Adapters.SmartFragmentStatePagerAdapter;
import com.fantasystock.fantasystock.Adapters.UsersArrayAdapter;
import com.fantasystock.fantasystock.Fragments.EditLeagueFragment;
import com.fantasystock.fantasystock.Fragments.LeagueFragment;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LeagueActivity extends AppCompatActivity implements EditLeagueFragment.EditLeagueDialogListener {
    @Bind(R.id.tvLeagueMemberNumber) TextView tvLeagueMemberNumber;
    @Bind(R.id.tvLeagueName) TextView tvLeagueName;
    @Bind(R.id.vpViewPager) ViewPager vpViewPager;
    @Bind(R.id.pgTabs) PagerSlidingTabStrip pgSlidingTab;
    ArrayList<User> rank;
    UsersArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        tvLeagueName.setText("Global League");
        vpViewPager.setAdapter(new LeaguePagerAdapter(getSupportFragmentManager()));
        pgSlidingTab.setViewPager(vpViewPager);
    }


    @Override
    public void onDone(HashSet<String> selectedUsers) {
        
    }
    @OnClick(R.id.tvSignout)
    public void onSignOutClick() {
        startActivityForResult(new Intent(getApplicationContext(), SignupActivity.class), 0);
        ParseUser.logOut();
        User.currentUser = null;
        finish();
    }
    private static class LeaguePagerAdapter extends FragmentPagerAdapter {

        public LeaguePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return LeagueFragment.newInstance(position==0);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }


}
