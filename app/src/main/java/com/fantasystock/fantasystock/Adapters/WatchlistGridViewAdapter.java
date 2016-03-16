package com.fantasystock.fantasystock.Adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Fragments.WindowChartView;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilsonsu on 3/16/16.
 */
public class WatchlistGridViewAdapter extends RecyclerView.Adapter<WindowChartView>  {
    private FragmentActivity fragmentActivity;
    private Drawable fadeBlue;
    private ArrayList<String> items;

    public WatchlistGridViewAdapter(FragmentActivity fragmentActivity, Drawable fadeBlue, ArrayList<String> items) {
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
    public void onBindViewHolder(WindowChartView holder, int position) {
        String symbol = items.get(position);
        final Stock stock = DataCenter.getInstance().stockMap.get(symbol);
        holder.setStock(stock);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
