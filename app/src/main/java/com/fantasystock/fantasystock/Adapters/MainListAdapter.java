package com.fantasystock.fantasystock.Adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;
    private View convertView;

    private final int STOCK = 0;
    private final int NEWS  = 1;
    private final int EXPANDALL = 2;
    private final int PROGRESS_BAR = 3;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public MainListAdapter(List<Object> items, RecyclerView recyclerView) {
        this.items = items;

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
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_progress, parent, false);

                viewHolder = new ProgressViewHolder(convertView);
                break;
            case EXPANDALL:
                convertView = inflater.inflate(R.layout.item_expand_all_main, parent, false);
                viewHolder = new ViewHolderExpandAll(convertView);
                break;
            case STOCK:
                convertView = inflater.inflate(R.layout.item_watchlist_main, parent, false);
                viewHolder = new ViewHolderStock(convertView);
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
        switch (getItemViewType(position)) {
            case PROGRESS_BAR:
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
                break;
            case EXPANDALL:
                bindViewHolderExpandAll((ViewHolderExpandAll) holder);
                break;
            case STOCK:
                bindViewHolderStock((ViewHolderStock) holder, (Stock) items.get(position));
                break;
            case NEWS:
            default:
                bindViewHolderNews((ViewHolderNews) holder, (News) items.get(position));
                break;
        }
    }

    private void bindViewHolderStock(ViewHolderStock holder, Stock stock) {
        holder.tvSymbol.setText(stock.symbol);
        holder.tvShare.setText(Integer.toString(stock.share) + " Shares");
        holder.tvChangePercentage.setText(stock.current_change_percentage);
        holder.tvCurrentPrice.setText(Float.toString(stock.current_price));
    }

    private void bindViewHolderNews(ViewHolderNews holder, News news) {
        holder.tvTitle.setText(news.title);
        holder.tvSummary.setText(news.title);
    }

    private void bindViewHolderExpandAll(ViewHolderExpandAll holder) {
        holder.tvExpandAll.setText("Expand All");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position) == null) return PROGRESS_BAR;
        else if(items.get(position) instanceof String)
            return EXPANDALL;
        else if(items.get(position) instanceof Stock)
            return STOCK;
        else
            return NEWS;
    }

    public class ViewHolderStock extends RecyclerView.ViewHolder {
        @Bind(R.id.tvSymbol) TextView tvSymbol;
        @Bind(R.id.tvShare) TextView tvShare;
        @Bind(R.id.tvChangePercentage) TextView tvChangePercentage;
        @Bind(R.id.tvCurrentPrice) TextView tvCurrentPrice;

        public ViewHolderStock(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderNews extends RecyclerView.ViewHolder {
        @Bind(R.id.tvTitle) TextView tvTitle;
        @Bind(R.id.tvSummary) TextView tvSummary;

        public ViewHolderNews(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderExpandAll extends RecyclerView.ViewHolder {
        @Bind(R.id.tvExpandAll) TextView tvExpandAll;

        public ViewHolderExpandAll(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
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
