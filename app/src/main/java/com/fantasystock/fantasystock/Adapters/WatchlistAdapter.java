package com.fantasystock.fantasystock.Adapters;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/11/16.
 */
public class WatchlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;
    private View convertView;
    private FragmentActivity fragmentActivity;
    private int STOCK_STATUS_FORMAT;
    private static final int REFRESH_WATCHLIST = 200;

    // Stock status types
    private final int CURRENT_PRICE = 0;
    private final int CHANGE_PERCENTAGE = 1;
    private final int CHANGE_PRICE = 2;

    // View Types
    private final int STOCK = 0;
    private final int TITLE_BAR = 1;


    private interface viewHolderBinding {
        void setItem(Object object, View view);
    }

    public WatchlistAdapter(List<Object> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.STOCK_STATUS_FORMAT = CURRENT_PRICE;
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TITLE_BAR:
                convertView = inflater.inflate(R.layout.item_title, parent, false);
                viewHolder = new ViewHolderTitleBar(convertView);
                break;
            default:
            case STOCK:
                convertView = inflater.inflate(R.layout.item_watchlist_main, parent, false);
                viewHolder = new ViewHolderStock(convertView, fragmentActivity);
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
        if(DataCenter.getInstance().stockMap.get(items.get(position)) == null)
            return TITLE_BAR;
        else
            return STOCK;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class ViewHolderStock extends RecyclerView.ViewHolder implements viewHolderBinding{
        private FragmentActivity fragmentActivity;
        @Bind(R.id.tvSymbol)
        TextView tvSymbol;
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvShare) TextView tvShare;
        @Bind(R.id.btnStatus)
        Button btnStatus;

        public ViewHolderStock(View itemView, FragmentActivity fragmentActivity) {
            super(itemView);
            this.fragmentActivity = fragmentActivity;
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItem(Object object, View view) {
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
                    fragmentActivity.startActivityForResult(intent, REFRESH_WATCHLIST);
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
}
