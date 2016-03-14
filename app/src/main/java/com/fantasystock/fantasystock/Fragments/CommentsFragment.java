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
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Comment;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/13/16.
 */
public class CommentsFragment extends Fragment{
    private ArrayList<Comment> comments;
    private CommentsArrayAdapter adapter;
    private String symbol;
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.etCommentText) EditText etCommentText;
    @Bind(R.id.btnSend) Button btnSend;

    public static CommentsFragment newInstance(String symbol) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getArguments().getString("symbol");
        comments = new ArrayList<>();
        adapter = new CommentsArrayAdapter(comments);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setSymbol(symbol);
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        Comment.getComments(symbol, new CallBack() {
            @Override
            public void commentsCallBack(ArrayList<Comment> returnComments) {
                comments.clear();
                comments.addAll(returnComments);
                adapter.notifyDataSetChanged();
            }
        });
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

    private static class CommentsArrayAdapter extends RecyclerView.Adapter<CommentsViewHolder> {
        private ArrayList<Comment> comments;

        public CommentsArrayAdapter(ArrayList<Comment> comments) {
            this.comments = comments;
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentsViewHolder holder, int position) {
            holder.setComment(comments.get(position));
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }
    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivUserProfile) ImageView ivUserProfile;
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvCommentTime) TextView tvCommentTime;
        @Bind(R.id.tvComment) TextView tvComment;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void setComment(Comment comment) {
            tvCommentTime.setText(Utils.converTimetoRelativeTime(comment.updatedAt));
            tvComment.setText(comment.data.comment);
            User.queryUser(comment.data.userId, new CallBack() {
                @Override
                public void done(Object object) {
                    if (object == null || !(object instanceof ParseUser)) {
                        return;
                    }
                    ParseUser parseUser = (ParseUser) object;
                    User user = new User(parseUser);
                    if (user == null ) {
                        return;
                    }
                    tvName.setText(user.username);
                    if (user.profileImageUrl == null ) {
                        ivUserProfile.setImageResource(R.drawable.ic_profile);
                    }
                    ivUserProfile.setImageResource(0);
                    Context context = ivUserProfile.getContext();
                    String url = user.profileImageUrl;
                    Glide.with(context).load(url).fitCenter().placeholder(R.drawable.ic_profile).into(ivUserProfile);
                }
            });

        }
    }

}
