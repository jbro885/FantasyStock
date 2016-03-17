package com.fantasystock.fantasystock.Adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Fragments.WindowChartView;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.List;

/**
 * Created by chengfu_lin on 3/17/16.
 */
public class WatchlistGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private FragmentActivity fragmentActivity;
    private Drawable fadeBlue;
    private List<Object> items;

    public WatchlistGridAdapter(List<Object> items, FragmentActivity fragmentActivity, Drawable fadeBlue) {
        this.fragmentActivity = fragmentActivity;
        this.fadeBlue = fadeBlue;
        this.items = items;
    }

    @Override
    public WindowChartView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.fragment_window_chart, parent, false);
        WindowChartView viewHolder = new WindowChartView(convertView, fadeBlue, fragmentActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String symbol = (String) items.get(position);
        final Stock stock = DataCenter.getInstance().stockMap.get(symbol);
        ((WindowChartView)holder).setStock(stock);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}
