package com.fantasystock.fantasystock.ViewHolder;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.DetailActivity;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.Utils;
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
        tvPrice.setText(Utils.moneyConverter(stockData.current_price));
        tvChanges.setText(" ( " + stockData.current_change_percentage + "% )");
        Float changePercentage = -100.0f;
        try {
            changePercentage = Float.parseFloat(stockData.current_change_percentage);
        } catch (Exception e) {

        }

        int color = fragmentActivity.getResources().getColor(R.color.green);
        if(changePercentage < 0) {
            color = fragmentActivity.getResources().getColor(R.color.red);
        }

        tvPrice.setTextColor(color);
        tvChanges.setTextColor(color);
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
