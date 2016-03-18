package com.fantasystock.fantasystock.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;

/**
 * Created by wilsonsu on 3/15/16.
 */
public class WindowChartView extends ChartView {
    @Bind(R.id.tvSymbol) TextView tvSymbol;
    @Bind(R.id.tvPrice) TextView tvPrice;
    @Bind(R.id.tvChanges) TextView tvChanges;

    public WindowChartView(View itemView, FragmentActivity fragmentActivity) {
        super(itemView, fragmentActivity);
    }

    @Override
    protected void onRefreshPeriod(String period) {
        super.onRefreshPeriod(period);
        tvSymbol.setText(stock.symbol);
        Stock stockData = DataCenter.getInstance().stockMap.get(stock.symbol);
        if( stockData == null) {
            return;
        }
        tvPrice.setText(stockData.current_price + "");
        tvChanges.setText(" (" + stockData.current_change_percentage + ")");
        Float changePercentage = -100.0f;
        try {
            changePercentage = Float.parseFloat(stockData.current_change_percentage);
        } catch (Exception e) {

        }

        if (changePercentage<0) {
            tvChanges.setTextColor(Color.parseColor("#f0162f"));
        } else {
            tvChanges.setTextColor(Color.parseColor("#13CC52"));
        }


    }

    @Override
    public void setStock(final Stock stock) {
        super.setStock(stock);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragmentActivity.getApplicationContext(), DetailActivity.class);
                intent.putExtra("symbol", stock.symbol);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragmentActivity, itemView, "windowCharts");
                fragmentActivity.startActivity(intent, options.toBundle());
            }
        });
    }
}
