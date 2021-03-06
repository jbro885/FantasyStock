package com.fantasystock.fantasystock.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.SeeMoreActivity;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Models.Comment;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/19/16.
 */
public class BriefCommentsFragment extends CommentsFragment{
    @Bind(R.id.btnLeaveComment) Button btnLeaveComment;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    @Bind(R.id.tvLoading) TextView tvLoading;

    public static BriefCommentsFragment newInstance(String symbol) {
        BriefCommentsFragment fragment = new BriefCommentsFragment();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brief_comment, container, false);
        ButterKnife.bind(this, view);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        prLoadingSpinner.setVisibility(View.INVISIBLE);

        return view;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        prLoadingSpinner.setVisibility(View.VISIBLE);
        btnLeaveComment.setVisibility(View.INVISIBLE);
        tvLoading.setText("");
        Comment.getComments(symbol, new CallBack() {
            @Override
            public void commentsCallBack(ArrayList<Comment> returnComments) {
                prLoadingSpinner.setVisibility(View.INVISIBLE);
                btnLeaveComment.setVisibility(View.VISIBLE);
                if (returnComments.size() > 5) {
                    btnLeaveComment.setText("See All Comments");
                } else {
                    btnLeaveComment.setText("Post Comments");
                }
                comments.clear();

                int len = Math.min(returnComments.size(), 5);
                if (len==0) {
                    tvLoading.setText("No one has commented on this stock yet...");
                    return;
                }
                tvLoading.setText("");
                for (int i = 0; i < len; ++i) {
                    comments.add(returnComments.get(i));
                }
                adapter.notifyDataSetChanged();

            }
        });
    }

    @OnClick(R.id.btnLeaveComment)
    public void onLeaveComment() {
        Intent intent = SeeMoreActivity.newIntent(getContext(), symbol, true);
        startActivity(intent);
    }
}
