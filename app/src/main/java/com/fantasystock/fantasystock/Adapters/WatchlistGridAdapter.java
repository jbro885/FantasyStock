package com.fantasystock.fantasystock.Adapters;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.ViewHolder.WindowChartView;
import com.fantasystock.fantasystock.Helpers.GridItemTouchHelperCallback;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by chengfu_lin on 3/17/16.
 */
public class WatchlistGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GridItemTouchHelperCallback.ItemTouchHelperAdapter{
    private FragmentActivity fragmentActivity;
    private List<Object> items;

    private OnStartDragListener onStartDragListener;

    public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
    }

    public WatchlistGridAdapter(List<Object> items, FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        this.items = items;
    }

    @Override
    public WindowChartView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.fragment_window_chart, parent, false);
        GridChartViewHolder viewHolder = new GridChartViewHolder(convertView, fragmentActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String symbol = (String) items.get(position);
        ((GridChartViewHolder)holder).setStock(symbol);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class GridChartViewHolder extends WindowChartView implements ItemTouchHelperViewHolder {

        public GridChartViewHolder(View itemView, FragmentActivity fragmentActivity) {
            super(itemView, fragmentActivity);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void setStock(String symbol) {
            super.setStock(symbol);
            final RecyclerView.ViewHolder holder = this;
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onStartDragListener.onStartDrag(holder);
                    return true;
                }
            });
        }
    }


    public interface ItemTouchHelperViewHolder {
        void onItemSelected();
        void onItemClear();
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // Swap items in DataCenter
        Collections.swap(User.currentUser.watchlist, fromPosition, toPosition);

        // Swap items on rvList
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        Stock stock = User.currentUser.investingStocksMap.get(items.get(position));

        if(stock == null || stock.share == 0) {
            stock = DataCenter.getInstance().stockMap.get(items.get(position));
            // Delete item in watchlist
            DataCenter.getInstance().unfavoriteStock(stock);
            // Delete item on rvList
            items.remove(position);
            notifyItemRemoved(position);
        }
        else {
            notifyItemChanged(position);
        }
    }
}
