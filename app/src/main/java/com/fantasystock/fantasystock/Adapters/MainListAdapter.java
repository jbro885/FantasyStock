package com.fantasystock.fantasystock.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private final int WATCHLIST_SIZE = 4;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
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
        // if(position == WATCHLIST_SIZE) return EXPANDALL;
        if(items.get(position) instanceof Stock)
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
}
