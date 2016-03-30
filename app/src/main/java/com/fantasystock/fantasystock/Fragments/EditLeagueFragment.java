package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
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
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Activities.LeagueActivity;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/29/16.
 */
public class EditLeagueFragment extends DialogFragment {
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.etSearchFriend) EditText etSearchFriend;
    @Bind(R.id.etLeagueName) EditText etLeagueName;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;

    ArrayList<User> users;
    LeagueActivity.UsersArrayAdapter adapter;

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
        adapter = new LeagueActivity.UsersArrayAdapter(users);
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


}
