package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Adapters.UsersArrayAdapter;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.ViewHolder.UserViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 4/3/16.
 */
public class LeagueFragment extends Fragment {
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    private boolean isGlobal;
    private ArrayList<User> users;
    private UsersArrayAdapter adapter;

    @Bind(R.id.vhCurrentUser) View vhCurrentUser;
    @Bind(R.id.rlCurrentUser) RelativeLayout rlCurrentUser;
    private UserViewHolder userViewHolder;
    private LinearLayoutManager linearLayoutManager;
    private int currentUserPosition;

    public static LeagueFragment newInstance(boolean isGlobal) {
        LeagueFragment fragment = new LeagueFragment();
        Bundle args = new Bundle();
        args.putBoolean("isGlobal", isGlobal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isGlobal= getArguments().getBoolean("isGlobal");
        }
        users = new ArrayList<>();
        adapter = new UsersArrayAdapter(users, getContext());
        currentUserPosition = -1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, view);

        rvList.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (currentUserPosition==-1) {
                    rlCurrentUser.setVisibility(View.INVISIBLE);
                    return;
                }
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() > currentUserPosition) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlCurrentUser.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlCurrentUser.setLayoutParams(params);
                    rlCurrentUser.setVisibility(View.VISIBLE);
                } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < currentUserPosition) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlCurrentUser.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    rlCurrentUser.setLayoutParams(params);
                    rlCurrentUser.setVisibility(View.VISIBLE);
                } else {
                    rlCurrentUser.setVisibility(View.INVISIBLE);
                }

            }
        });
        prLoadingSpinner.setVisibility(View.INVISIBLE);
        userViewHolder = new UserViewHolder(vhCurrentUser, getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<User> users = User.getAllUsers();

        // loading users:
        if (users == null || users.size()==0) {
            prLoadingSpinner.setVisibility(View.VISIBLE);
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
    }

    private void setUsers(ArrayList<User> AllUsers) {
        ArrayList<User> addUsers = new ArrayList<User>();
        if (!isGlobal) {
            ArrayList<String> userIds = User.currentUser.followings;
            int len = userIds.size();
            for (int i=0;i<len; ++i) {
                if (User.userMap.containsKey(userIds.get(i))) {
                    addUsers.add(User.userMap.get(userIds.get(i)));
                }
            }
            addUsers.add(User.currentUser);
        }
        else {
            addUsers.addAll(AllUsers);

        }
        Collections.sort(addUsers, new Comparator<User>() {
            public int compare(User u1, User u2) {
                if (u1.totalValue == u2.totalValue)
                    return 0;
                return u1.totalValue < u2.totalValue ? 1 : -1;
            }
        });


        users.clear();
        users.addAll(addUsers);
        adapter.notifyDataSetChanged();
        prLoadingSpinner.setVisibility(View.INVISIBLE);

        int len = users.size();
        for (int i=0;i<len;++i) {
            if (users.get(i).id.equals(User.currentUser.id)) {
                userViewHolder.setUser(User.currentUser, i+1);
                currentUserPosition = i;
                break;
            }
        }
    }
}
