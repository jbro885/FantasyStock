package com.fantasystock.fantasystock.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.ViewHolder.UserViewHolder;

import java.util.ArrayList;

/**
 * Created by chengfu_lin on 3/31/16.
 */
public class UsersArrayAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private ArrayList<User> rank;
    private Context context;

    public UsersArrayAdapter(ArrayList<User> rank, Context context) {
        this.rank = rank;
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user_rank, parent, false);
        return new UserViewHolder(view, context);
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