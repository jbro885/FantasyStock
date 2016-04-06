package com.fantasystock.fantasystock.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 4/3/16.
 */
public class WatchlistProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> items;
    private View convertView;
    private FragmentActivity fragmentActivity;
    private boolean isDarkTheme;
    private int STOCK_STATUS_FORMAT;
    private static final int REFRESH_WATCHLIST = 200;
    private Intent detailIntent;

    // Stock status types
    private final int CURRENT_PRICE = 0;
    private final int CHANGE_PERCENTAGE = 1;
    private final int CHANGE_PRICE = 2;

    public interface viewHolderBinding {
        void setItem(Object object, View view);
    }


    public WatchlistProfileAdapter(ArrayList<String> items, FragmentActivity fragmentActivity, boolean isDarkTheme) {
        this.items = items;
        this.isDarkTheme = isDarkTheme;
        this.STOCK_STATUS_FORMAT = CURRENT_PRICE;
        this.fragmentActivity = fragmentActivity;
        detailIntent= new Intent(fragmentActivity.getApplicationContext(), DetailActivity.class);
        detailIntent.putStringArrayListExtra("symbols",items);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_watchlist, parent, false);
        viewHolder = new ViewHolderStock(convertView, fragmentActivity, isDarkTheme, detailIntent);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        viewHolderBinding viewHolder = (viewHolderBinding)holder;
        viewHolder.setItem(items.get(position), convertView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class ViewHolderStock extends RecyclerView.ViewHolder implements viewHolderBinding {
        private FragmentActivity fragmentActivity;
        @Bind(R.id.tvSymbol) TextView tvSymbol;
        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvShare) TextView tvShare;
        @Bind(R.id.tvEquityValue) TextView tvEquityValue;
        @Bind(R.id.btnStatus) Button btnStatus;
        private Intent intent;

        public ViewHolderStock(View itemView, FragmentActivity fragmentActivity, boolean isDarkTheme, Intent intent) {
            super(itemView);
            this.fragmentActivity = fragmentActivity;
            this.intent = intent;
            ButterKnife.bind(this, itemView);
            tvSymbol.setTextColor((isDarkTheme) ? Color.WHITE : Color.BLACK);
            tvShare.setTextColor((isDarkTheme) ? Color.WHITE : Color.BLACK);
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
            String shareStatus = "";
            String valueStatus = "";
            if (User.currentUser.investingStocksMap.containsKey(stock.symbol)) {
                Stock investingStock = User.currentUser.investingStocksMap.get(stock.symbol);
                if (investingStock!=null) {
                    int share = investingStock.share;
                    if(share > 0) {
                        shareStatus = Integer.toString(share) + " Shares";
                        float value = stock.current_price * (float) share;
                        valueStatus = Utils.moneyConverter(value);
                    }
                }
            }

            tvShare.setText(shareStatus);
            tvEquityValue.setText(valueStatus);
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
            Float currentChange = 0.0f;
            try {
                currentChange = Float.parseFloat(stock.current_change);
            } catch (Exception e) {

            }
            if(currentChange < 0.0f) {
                btnStatus.setSelected(false);
            }
            else {
                btnStatus.setSelected(true);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    status = "$" + Float.toString(stock.current_price);
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
}