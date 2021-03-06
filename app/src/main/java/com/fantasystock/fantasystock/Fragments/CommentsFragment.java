package com.fantasystock.fantasystock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.UserProfileActivity;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.Comment;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/18/16.
 */
public class CommentsFragment extends Fragment {
    protected ArrayList<Comment> comments;
    protected CommentsArrayAdapter adapter;
    protected String symbol;
    @Bind(R.id.rvList) RecyclerView rvList;

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
        adapter = new CommentsArrayAdapter(comments, getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
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


    protected static class CommentsArrayAdapter extends RecyclerView.Adapter<CommentsViewHolder> {
        private ArrayList<Comment> comments;
        private Context context;

        public CommentsArrayAdapter(ArrayList<Comment> comments, Context context) {
            this.context = context;
            this.comments = comments;
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentsViewHolder(view, context);
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
        @Bind(R.id.tvRank) TextView tvCommentTime;
        @Bind(R.id.tvComment) TextView tvComment;
        private Context context;

        public CommentsViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }
        public void setComment(Comment comment) {
            tvCommentTime.setText(Utils.converTimetoRelativeTime(comment.updatedAt));
            tvComment.setText(comment.data.comment);
            User.queryUserId(comment.data.userId, new CallBack() {

                @Override
                public void userCallBack(final User user) {
                    tvName.setText(user.username);
                    if (user.profileImageUrl == null) {
                        ivUserProfile.setImageResource(R.drawable.ic_profile);
                        return;
                    }
                    ivUserProfile.setImageResource(0);
                    Utils.setupProfileImage(ivUserProfile, user.profileImageUrl);
                    ivUserProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, UserProfileActivity.class);
                            intent.putExtra("userId", user.id);
                            context.startActivity(intent);

                        }
                    });
                }
            });



        }
    }

}
