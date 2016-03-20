package com.fantasystock.fantasystock.Adapters;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.Activities.NewsActivity;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> items;
    private View convertView;
    private FragmentActivity fragmentActivity;


    // View Types
    private final int NEWS  = 0;
    private final int PROGRESS_BAR = 1;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private interface viewHolderBinding {
        void setItem(Object object, View view);
    }

    public NewsListAdapter(List<News> items, RecyclerView recyclerView, FragmentActivity fragmentActivity) {
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
            case NEWS:
            default:
                convertView = inflater.inflate(R.layout.item_news, parent, false);
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
        else
            return NEWS;
    }

    public class ViewHolderNews extends RecyclerView.ViewHolder implements viewHolderBinding{
        @Bind(R.id.tvTitle) TextView tvTitle;
        @Bind(R.id.tvSummary) TextView tvSummary;
        @Bind(R.id.tvPublished) TextView tvPublished;
        @Bind(R.id.tvPublisher) TextView tvPublisher;
        @Bind(R.id.tvRelatedStocks) TextView tvRelatedStocks;
        @Bind(R.id.ivImage) ImageView ivImage;
        private News news;

        public ViewHolderNews(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object, View view) {
            final News news = (News)object;
            this.news = news;

            tvTitle.setText(news.title);
            tvSummary.setText(news.summary);
            tvSummary.setMaxLines(3);
            if (news.published!=null) {
                tvPublished.setText(Utils.converTimetoRelativeTime(news.published));
            }
            tvPublisher.setText(news.publisher);

            if (news.entities.size()>0) {
                int len = news.entities.size();
                String relativeStocks = "";
                for (int i = 0; i < Math.min(len,5); ++i) {
                    relativeStocks += news.entities.get(i).term.replace("TICKER:","") + "  ";
                }
                tvRelatedStocks.setText(relativeStocks);
            } else {
                tvRelatedStocks.setText("");
            }

            if(news.images != null) {
                ivImage.getLayoutParams().width = (int) view.getResources().getDimension(R.dimen.news_image_width);
                Glide.with(convertView.getContext())
                        .load(news.images.get(0).url)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivImage);
            }
            else {
                ivImage.getLayoutParams().width = 0;
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NewsActivity.class);
                    String newsString = new Gson().toJson(news);
                    intent.putExtra("newsString", newsString);

                    Pair<View, String> p1 = Pair.create((View) tvTitle, "newsTitle");
                    Pair<View, String> p2 = Pair.create((View) ivImage, "newsImage");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragmentActivity, p1, p2);
                    fragmentActivity.startActivity(intent, options.toBundle());
                }
            });
        }


        @OnClick(R.id.tvRelatedStocks)
        public void onRelatedStocks() {
            Intent intent = new Intent(tvRelatedStocks.getContext(), DetailActivity.class);
            int len = news.entities.size();
            ArrayList<String> relativeStocks = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                relativeStocks.add(news.entities.get(i).term.replace("TICKER:",""));
            }
            intent.putStringArrayListExtra("symbols",relativeStocks);
            fragmentActivity.startActivity(intent);
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
