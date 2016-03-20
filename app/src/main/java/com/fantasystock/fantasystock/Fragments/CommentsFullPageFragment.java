package com.fantasystock.fantasystock.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Models.Comment;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/13/16.
 */
public class CommentsFullPageFragment extends CommentsFragment{
    @Bind(R.id.etCommentText) EditText etCommentText;
    @Bind(R.id.btnSend) Button btnSend;

    public static CommentsFullPageFragment newInstance(String symbol) {
        CommentsFullPageFragment fragment = new CommentsFullPageFragment();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.bind(this, view);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }


    @OnClick(R.id.btnSend)
    public void onSend() {
        Comment.addComment(etCommentText.getText().toString(), symbol, new CallBack() {
            @Override
            public void commentCallBack(Comment comment) {
                comments.add(comment);
                adapter.notifyDataSetChanged();
                etCommentText.setText("");
            }
        });
    }
}
