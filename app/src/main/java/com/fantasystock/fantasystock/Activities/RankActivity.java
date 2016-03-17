package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.Models.Comment;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        });
    }

    private static class UsersArrayAdapter extends RecyclerView.Adapter<UserViewHolder> {
        private ArrayList<User> rank;

        public UsersArrayAdapter(ArrayList<User> rank) {
            this.rank = rank;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
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
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvCommentTime) TextView tvCommentTime;
        @Bind(R.id.tvComment) TextView tvComment;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void setUser(User user, int place) {
            tvCommentTime.setText(""+place);
            if (user == null) {
                return;
            }
            tvComment.setText(user.totalValue+"");
            tvName.setText(user.username);
            if (user.profileImageUrl == null) {
                ivUserProfile.setImageResource(R.drawable.ic_profile);
            }
            ivUserProfile.setImageResource(0);
            Context context = ivUserProfile.getContext();
            String url = user.profileImageUrl;
            Glide.with(context).load(url).fitCenter().placeholder(R.drawable.ic_profile).into(ivUserProfile);

        }
    }

}
