package com.fantasystock.fantasystock.Adapters;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fantasystock.fantasystock.DataClient;
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
    private int STOCK_STATUS_FORMAT;

    // Stock status types
    private final int CURRENT_PRICE = 0;
    private final int CHANGE_PERCENTAGE = 1;
    private final int CHANGE_PRICE = 2;

    // View Types
    private final int STOCK = 0;
    private final int NEWS  = 1;
    private final int TITLE_BAR = 2;
    private final int PROGRESS_BAR = 3;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public MainListAdapter(List<Object> items, RecyclerView recyclerView) {
        this.items = items;
        this.STOCK_STATUS_FORMAT = CURRENT_PRICE;

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
            case TITLE_BAR:
                convertView = inflater.inflate(R.layout.item_title, parent, false);
                viewHolder = new ViewHolderTitleBar(convertView);
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
            case TITLE_BAR:
                bindViewHolderTitleBar((ViewHolderTitleBar) holder, (String) items.get(position));
                break;
            case STOCK:
                bindViewHolderStock((ViewHolderStock) holder, (String) items.get(position));
                break;
            case NEWS:
            default:
                bindViewHolderNews((ViewHolderNews) holder, (News) items.get(position));
                break;
        }
    }

    private void bindViewHolderStock(final ViewHolderStock holder, String symbol) {
        final Stock stock = DataClient.stockMap.get(symbol);

        holder.tvSymbol.setText(stock.symbol);
        holder.tvName.setText(stock.name);

        String shareStatus = Integer.toString(stock.share) + " Shares";
        holder.tvShare.setText(shareStatus);
        // default is current price, click will be change percentage
        btnStatusDisplay(holder, stock);
        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STOCK_STATUS_FORMAT++;
                STOCK_STATUS_FORMAT = STOCK_STATUS_FORMAT % 3;
                notifyDataSetChanged();
            }
        });
        // Display background color based on change price
        if(Float.parseFloat(stock.current_change) < 0) {
            holder.btnStatus.setSelected(false);
        }
        else {
            holder.btnStatus.setSelected(true);
        }
    }

    private void btnStatusDisplay(final ViewHolderStock holder, Stock stock) {
        final String status;
        switch(STOCK_STATUS_FORMAT) {
            default:
            case CURRENT_PRICE:
                status = Float.toString(stock.current_price);
                break;
            case CHANGE_PERCENTAGE:
                status = stock.current_change_percentage + "%";
                break;
            case CHANGE_PRICE:
                status = stock.current_change;
                break;
        }
        holder.btnStatus.setAlpha(0);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.btnStatus.setAlpha(1);
                holder.btnStatus.setText(status);
            }
        }, 3);
    }

    private void bindViewHolderNews(ViewHolderNews holder, News news) {
        holder.tvTitle.setText(news.title);
        holder.tvSummary.setText(news.title);
    }

    private void bindViewHolderTitleBar(ViewHolderTitleBar holder, String title) {
        holder.tvTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position) == null) return PROGRESS_BAR;
        else if(items.get(position) instanceof String) {
            // can be stock symbol or title
            if(DataClient.stockMap.get(items.get(position)) == null)
                return TITLE_BAR;
            else
                return STOCK;
        }
        else
            return NEWS;
    }

    public class ViewHolderStock extends RecyclerView.ViewHolder {
        @Bind(R.id.tvSymbol) TextView tvSymbol;
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvShare) TextView tvShare;
        @Bind(R.id.btnStatus) Button btnStatus;

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

    public class ViewHolderTitleBar extends RecyclerView.ViewHolder {
        @Bind(R.id.tvTitle) TextView tvTitle;

        public ViewHolderTitleBar(View itemView) {
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
