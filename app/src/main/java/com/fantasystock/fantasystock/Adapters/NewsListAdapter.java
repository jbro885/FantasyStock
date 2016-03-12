package com.fantasystock.fantasystock.Adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;
    private View convertView;
    private FragmentActivity fragmentActivity;


    // View Types
    private final int NEWS  = 1;
    private final int TITLE_BAR = 2;
    private final int PROGRESS_BAR = 3;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private interface viewHolderBinding {
        void setItem(Object object, View view);
    }

    public NewsListAdapter(List<Object> items, RecyclerView recyclerView, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentActivity = fragmentActivity;

        // Set up scrolling listener
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case PROGRESS_BAR:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
                viewHolder = new ProgressViewHolder(convertView);
                break;
            case TITLE_BAR:
                convertView = inflater.inflate(R.layout.item_title, parent, false);
                viewHolder = new ViewHolderTitleBar(convertView);
                break;
            case NEWS:
            default:
                convertView = inflater.inflate(R.layout.item_news_main, parent, false);
                viewHolder = new ViewHolderNews(convertView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof viewHolderBinding) {
            viewHolderBinding viewHolder = (viewHolderBinding)holder;
            viewHolder.setItem(items.get(position), convertView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position) == null) return PROGRESS_BAR;
        else if(items.get(position) instanceof String)
            return TITLE_BAR;
        else
            return NEWS;
    }

    public class ViewHolderNews extends RecyclerView.ViewHolder implements viewHolderBinding{
        @Bind(R.id.tvTitle) TextView tvTitle;
        @Bind(R.id.tvId) TextView tvId;
        @Bind(R.id.tvAuthor) TextView tvAuthor;
        @Bind(R.id.tvPublished) TextView tvPublished;
        @Bind(R.id.tvPublisher) TextView tvPublisher;
        @Bind(R.id.tvSummary) TextView tvSummary;
        @Bind(R.id.ivImage) ImageView ivImage;

        public ViewHolderNews(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object, View view) {
            News news = (News)object;
            tvTitle.setText(news.title);
            tvId.setText(news.id);
            tvAuthor.setText(news.author);
            tvPublished.setText(news.published);
            tvPublisher.setText(news.publisher);
            tvSummary.setText(news.title);
            if(news.images != null) {
                Glide.with(convertView.getContext())
                        .load(news.images.get(0).url)
                        .into(ivImage);
            }
        }
    }

    public class ViewHolderTitleBar extends RecyclerView.ViewHolder implements viewHolderBinding{
        @Bind(R.id.tvTitle) TextView tvTitle;

        public ViewHolderTitleBar(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object, View view) {
            String title = (String)object;
            tvTitle.setText(title);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder implements viewHolderBinding{
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        @Override
        public void setItem(Object object, View view) {
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}