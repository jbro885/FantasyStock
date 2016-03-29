package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.Fragments.EditLeagueFragment;
import com.fantasystock.fantasystock.Fragments.TradeFragment;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LeagueActivity extends AppCompatActivity {
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    @Bind(R.id.tvLeagueMemberNumber) TextView tvLeagueMemberNumber;
    @Bind(R.id.tvLeagueName) TextView tvLeagueName;
    @Bind(R.id.fab) FloatingActionButton createLeagueButton;

    ArrayList<User> rank;
    UsersArrayAdapter adapter;

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

    public static class UsersArrayAdapter extends RecyclerView.Adapter<UserViewHolder> {
        private ArrayList<User> rank;

        public UsersArrayAdapter(ArrayList<User> rank) {
            this.rank = rank;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_user_rank, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            holder.setUser(rank.get(position), position + 1);
        }

        @Override
        public int getItemCount() {
            return rank.size();
        }
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.tvRank) TextView tvRank;
        @Bind(R.id.tvPortfolio) TextView tvPortfolio;
        @Bind(R.id.ibChampion)
        ImageButton ibChampion;
        @Bind(R.id.rlItem) RelativeLayout rlItem;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void setUser(User user, int place) {
            if (user == null) return;

            tvName.setText(user.username);
            tvRank.setText(Integer.toString(place));
            tvPortfolio.setText(user.totalValue + "");

            if (user.profileImageUrl == null) {
                ivUserProfile.setImageResource(R.drawable.ic_profile);
            }
            else {
                ivUserProfile.setImageResource(0);
                Utils.setupProfileImage(ivUserProfile, user.profileImageUrl);
            }
            // Mark the first place
            if(place == 1) {
                ibChampion.setAlpha((float) 1.0);
            }
            else {
                ibChampion.setAlpha((float) 0.0);
            }
            // Mark current user
            if(user.username.equals(User.currentUser.username)) {
                rlItem.setBackgroundColor(Color.parseColor("#FFB7DAD9"));
            }
        }
    }

    @OnClick(R.id.tvSignout)
    public void onSignOutClick() {
        startActivityForResult(new Intent(getApplicationContext(), SignupActivity.class), 0);
        ParseUser.logOut();
        User.currentUser = null;
    }
    @OnClick(R.id.fab)
    public void onCreateLeague() {
        EditLeagueFragment editLeagueFragment = new EditLeagueFragment();
        editLeagueFragment.show(getSupportFragmentManager(), "200");
    }

}
