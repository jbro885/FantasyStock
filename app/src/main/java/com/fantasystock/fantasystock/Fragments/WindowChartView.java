package com.fantasystock.fantasystock.Fragments;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;

/**
 * Created by wilsonsu on 3/15/16.
 */
public class WindowChartView extends ChartView {
    @Bind(R.id.tvSymbol)
    TextView tvSymbol;
    @Bind(R.id.tvPrice) TextView tvPrice;
    @Bind(R.id.tvChanges) TextView tvChanges;

    public WindowChartView(View itemView, Drawable fadeBlue) {
        super(itemView, fadeBlue);
    }

    @Override
    protected void onRefreshPeriod(String period) {
        super.onRefreshPeriod(period);
        tvSymbol.setText(stock.symbol);
        Stock stockData = DataCenter.getInstance().stockMap.get(stock.symbol);
        if( stockData == null) {
            return;
        }
        tvPrice.setText(stockData.current_price+"");
        tvChanges.setText(" (" + stockData.current_change_percentage + ")");
    }
}
