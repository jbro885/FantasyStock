package com.fantasystock.fantasystock.Adapters;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.Activities.SearchActivity;
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;
    private View convertView;
    private FragmentActivity fragmentActivity;
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
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private interface viewHolderBinding {
        public void setItem(Object object);
    }

    public MainListAdapter(List<Object> items, RecyclerView recyclerView, FragmentActivity fragmentActivity) {
        this.items = items;
        this.STOCK_STATUS_FORMAT = CURRENT_PRICE;
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
            case STOCK:
                convertView = inflater.inflate(R.layout.item_watchlist_main, parent, false);
                viewHolder = new ViewHolderStock(convertView, fragmentActivity);
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
            viewHolder.setItem(items.get(position));
        }
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
            if(DataCenter.getInstance().stockMap.get(items.get(position)) == null)
                return TITLE_BAR;
            else
                return STOCK;
        }
        else
            return NEWS;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class ViewHolderStock extends RecyclerView.ViewHolder implements viewHolderBinding{
        private FragmentActivity fragmentActivity;
        @Bind(R.id.tvSymbol) TextView tvSymbol;
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvShare) TextView tvShare;
        @Bind(R.id.btnStatus) Button btnStatus;

        public ViewHolderStock(View itemView, FragmentActivity fragmentActivity) {
            super(itemView);
            this.fragmentActivity = fragmentActivity;
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object) {
            if (!(object instanceof String)) {
                return;
            }
            final String symbol = (String)object;
            final Stock stock = DataCenter.getInstance().stockMap.get(symbol);

            tvSymbol.setText(stock.symbol);
            tvName.setText(stock.name);
            String shareStatus;
            if (DataCenter.getInstance().investingStocksMap.containsKey(stock.symbol)) {
                shareStatus = Integer.toString(DataCenter.getInstance().investingStocksMap.get(stock.symbol).share) + " Shares";
            } else {
                shareStatus = "";
            }

            tvShare.setText(shareStatus);
            // default is current price, click will be change percentage
            btnStatusDisplay(stock);
            btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    STOCK_STATUS_FORMAT++;
                    STOCK_STATUS_FORMAT = STOCK_STATUS_FORMAT % 3;
                    notifyDataSetChanged();
                }
            });
            // Display background color based on change price
            if(Float.parseFloat(stock.current_change) < 0) {
                btnStatus.setSelected(false);
            }
            else {
                btnStatus.setSelected(true);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(fragmentActivity.getApplicationContext(), DetailActivity.class);
                    intent.putExtra("symbol", symbol);
                    fragmentActivity.startActivity(intent);
                }
            });
        }

        private void btnStatusDisplay(Stock stock) {
            final String status = stockStatus(stock, STOCK_STATUS_FORMAT);
            final String prevStatus = stockStatus(stock, (STOCK_STATUS_FORMAT+2)%3);
            btnStatus.setText(prevStatus);
            Utils.fadeInAndOutAnimationGenerator(btnStatus, new CallBack() {
                @Override
                public void task() {
                    btnStatus.setText(status);
                }
            });
        }
        private String stockStatus(Stock stock, int statusCode) {
            String status;
            switch(statusCode) {
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
            return status;
        }
    }

    public class ViewHolderNews extends RecyclerView.ViewHolder implements viewHolderBinding{
        @Bind(R.id.tvTitle) TextView tvTitle;
        @Bind(R.id.tvSummary) TextView tvSummary;

        public ViewHolderNews(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object) {
            News news = (News)object;
            tvTitle.setText(news.title);
            tvSummary.setText(news.title);
        }
    }

    public class ViewHolderTitleBar extends RecyclerView.ViewHolder implements viewHolderBinding{
        @Bind(R.id.tvTitle) TextView tvTitle;

        public ViewHolderTitleBar(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object) {
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
        public void setItem(Object object) {
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
