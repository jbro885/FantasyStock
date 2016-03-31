package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.Adapters.UsersArrayAdapter;
import com.fantasystock.fantasystock.Fragments.EditLeagueFragment;
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
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    @Bind(R.id.tvLeagueMemberNumber) TextView tvLeagueMemberNumber;
    @Bind(R.id.tvLeagueName) TextView tvLeagueName;
    @Bind(R.id.fab) FloatingActionButton createLeagueButton;

    ArrayList<User> rank;
    UsersArrayAdapter adapter;
    private EditLeagueFragment editLeagueFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        rank = new ArrayList<>();
        adapter = new UsersArrayAdapter(rank);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        prLoadingSpinner.setVisibility(View.VISIBLE);
        ArrayList<User> users = User.getAllUsers();
        if (users == null || users.size()==0) {
            User.userRank(new CallBack(){
                @Override
                public void usersCallBack(ArrayList<User> users) {
                    setUsers(users);
                }
                @Override
                public void onFail(String failureMessage) {
                    Log.d("DEBUG", failureMessage);
                    prLoadingSpinner.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            setUsers(users);
        }

        tvLeagueName.setText("Global League");
    }
    private void setUsers(ArrayList<User> users) {
        rank.clear();
        rank.addAll(users);
        adapter.notifyDataSetChanged();
        prLoadingSpinner.setVisibility(View.INVISIBLE);
        tvLeagueMemberNumber.setText(users.size() + "members");
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
    @OnClick(R.id.fab)
    public void onCreateLeague() {
        editLeagueFragment = new EditLeagueFragment();
        editLeagueFragment.show(getSupportFragmentManager(), "200");
    }


}
