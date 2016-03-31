package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Adapters.UsersArrayAdapter;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RankActivity extends AppCompatActivity {
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;

    ArrayList<User> rank;
    UsersArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        rank = new ArrayList<>();
        adapter = new UsersArrayAdapter(rank);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        prLoadingSpinner.setVisibility(View.VISIBLE);
        User.userRank(new CallBack(){
            @Override
            public void usersCallBack(ArrayList<User> users) {
                rank.clear();
                rank.addAll(users);
                adapter.notifyDataSetChanged();
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFail(String failureMessage) {
                Log.d("DEBUG", failureMessage);
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.tvSignout)
    public void onSignOutClick() {
        ParseUser.logOut();
        User.currentUser = null;
        startActivityForResult(new Intent(getApplicationContext(), SignupActivity.class), 0);
        finish();
    }


}
