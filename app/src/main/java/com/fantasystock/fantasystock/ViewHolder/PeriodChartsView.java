package com.fantasystock.fantasystock.ViewHolder;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/6/16.
 */
public class PeriodChartsView extends ChartView {
    @Bind(R.id.tvPeriodOneDay) TextView tvPeriodOneDay;
    @Bind(R.id.tvPeriodOneWeek) TextView tvPeriodOneWeek;
    @Bind(R.id.tvPeriodOneMonth) TextView tvPeriodOneMonth;
    @Bind(R.id.tvPeriodAnYear) TextView tvPeriodAnYear;
    @Bind(R.id.tvPeriodHalfYear) TextView tvPeriodHalfYear;
    @Bind(R.id.tvPeriodALL) TextView tvPeriodALL;

    private static final String PERIOD_1D = "1d";
    private static final String PERIOD_1W = "5d";
    private static final String PERIOD_1M = "20d";
    private static final String PERIOD_6M = "6m";
    private static final String PERIOD_1Y = "1y";
    private static final String PERIOD_ALL = "10y";

    public PeriodChartsView(View itemView, FragmentActivity fragmentActivity) {
        super(itemView, fragmentActivity);
        ButterKnife.bind(this, itemView);

    }

    @Override
    public void setStock(Stock stock) {
        this.stock = stock;
        lineChart.getAxisLeft().setTextColor(isDarkTheme ? Color.WHITE : Color.BLACK);
        onOneDayClick();
    }

    @OnClick(R.id.tvPeriodALL)
    public void onAllClick() {onPeriodClick(PERIOD_ALL, tvPeriodALL); }
    @OnClick(R.id.tvPeriodAnYear)
    public void onYearClick() {onPeriodClick(PERIOD_1Y, tvPeriodAnYear); }
    @OnClick(R.id.tvPeriodHalfYear)
    public void onHalfYearClick() {onPeriodClick(PERIOD_6M, tvPeriodHalfYear); }
    @OnClick(R.id.tvPeriodOneMonth)
    public void onOneMonthClick() {onPeriodClick(PERIOD_1M, tvPeriodOneMonth); }
    @OnClick(R.id.tvPeriodOneDay)
    public void onOneDayClick() {onPeriodClick(PERIOD_1D, tvPeriodOneDay); }
    @OnClick(R.id.tvPeriodOneWeek)
    public void onOneWeekClick() {onPeriodClick(PERIOD_1W, tvPeriodOneWeek); }

    private void onPeriodClick(String period, TextView textView) {
        super.onRefreshPeriod(period);

        int greyColor = Color.parseColor("#7788AA");
        tvPeriodALL.setTextColor(greyColor);
        tvPeriodAnYear.setTextColor(greyColor);
        tvPeriodHalfYear.setTextColor(greyColor);
        tvPeriodOneDay.setTextColor(greyColor);
        tvPeriodOneWeek.setTextColor(greyColor);
        tvPeriodOneMonth.setTextColor(greyColor);
        textView.setTextColor(isDarkTheme?Color.WHITE:Color.BLACK);
    }
}
