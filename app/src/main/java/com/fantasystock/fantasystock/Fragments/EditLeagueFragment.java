package com.fantasystock.fantasystock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.LeagueActivity;
import com.fantasystock.fantasystock.Helpers.Blur;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/29/16.
 */
public class EditLeagueFragment extends DialogFragment {

    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.etSearchFriend) EditText etSearchFriend;
    @Bind(R.id.etLeagueName) EditText etLeagueName;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    @Bind(R.id.fab) FloatingActionButton fab;

    ArrayList<User> users;
    HashSet<String> selectedUsers;
    SelectUsersArrayAdapter adapter;

    public interface EditLeagueDialogListener {
        void onDone(HashSet<String> selectedUsers);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_league, container);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setLayout(400, 500);

        users = new ArrayList<>();
        selectedUsers = new HashSet<>();
        adapter = new SelectUsersArrayAdapter(users, selectedUsers);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryText = etSearchFriend.getText().toString();
                if (queryText.length() == 0) {
                    handleSearchResults(User.getAllUsers());
                    return;
                }
                prLoadingSpinner.setVisibility(View.VISIBLE);
                User.queryUser(queryText, new CallBack() {
                    @Override
                    public void usersCallBack(ArrayList<User> users) {
                        prLoadingSpinner.setVisibility(View.INVISIBLE);
                        handleSearchResults(users);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handleSearchResults(User.getAllUsers());
    }

    private void handleSearchResults(ArrayList<User> responseUsers) {
        users.clear();
        users.addAll(responseUsers);
        adapter.notifyDataSetChanged();
        prLoadingSpinner.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.fab)
    public void onDone() {
        EditLeagueDialogListener listener = (EditLeagueDialogListener) getActivity();
        listener.onDone(selectedUsers);
        // Close the dialog and return back to the parent activity
        dismiss();
    }

    public static class SelectUsersArrayAdapter extends RecyclerView.Adapter<SelectUserViewHolder> {
        private ArrayList<User> users;
        private HashSet<String> selectedUsers;

        public SelectUsersArrayAdapter(ArrayList<User> users, HashSet<String> selectedUsers) {
            this.users = users;
            this.selectedUsers = selectedUsers;
        }
        @Override
        public SelectUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_selection, parent, false);
            return new SelectUserViewHolder(view, selectedUsers);
        }

        @Override
        public void onBindViewHolder(SelectUserViewHolder holder, int position) {
            holder.setUser(users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    public static class SelectUserViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivUserProfile) ImageView ivUserProfile;
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvPortfolio) TextView tvPortfolio;
        @Bind(R.id.rlItem) RelativeLayout rlItem;
        @Bind(R.id.ibCircle) ImageButton ibCircle;
        @Bind(R.id.ibFavorite) ImageButton ibFavorite;
        private HashSet<String> selectedUsers;
        private User user;

        public SelectUserViewHolder(View itemView, HashSet<String> selectedUsers) {
            super(itemView);
            this.selectedUsers = selectedUsers;
            ButterKnife.bind(this, itemView);
            ibCircle.setColorFilter(Color.parseColor("#44b449"));
        }
        public void setUser(User user) {
            if (user == null) return;
            this.user = user;
            tvName.setText(user.username);
            tvPortfolio.setText(Utils.moneyConverter(user.totalValue));

            if (user.profileImageUrl == null) {
                ivUserProfile.setImageResource(R.drawable.ic_profile);
            }
            else {
                ivUserProfile.setImageResource(0);
                Utils.setupProfileImage(ivUserProfile, user.profileImageUrl);
            }
            reloadFavoriteIcon();
        }
        @OnClick(R.id.ibFavorite)
        public void onFavoriteOnClick() {
            if(selectedUsers.contains(user.id)) {
                selectedUsers.remove(user.id);
            } else {
                selectedUsers.add(user.id);
            }
            reloadFavoriteIcon();
        }

        private void reloadFavoriteIcon() {
            if(selectedUsers.contains(user.id)) {
                ibCircle.setAlpha(1.0f);
                ibFavorite.setImageResource(R.drawable.ic_check);
            }
            else {
                ibCircle.setAlpha(0.0f);
                ibFavorite.setImageResource(R.drawable.ic_add);
            }
        }
    }


}
