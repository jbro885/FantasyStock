package com.fantasystock.fantasystock.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.fantasystock.fantasystock.Fragments.DetailFragment;
import com.fantasystock.fantasystock.Models.User;

import java.util.ArrayList;

/**
 * Created by chengfu_lin on 3/30/16.
 */
public abstract class DetailsPagerAdapter extends SmartFragmentStatePagerAdapter{
    private FragmentActivity fragmentActivity;
    private ArrayList<String> stocks;
    public DetailsPagerAdapter(FragmentManager fm, ArrayList<String> stocks, FragmentActivity fragmentActivity) {
        super(fm);
        this.fragmentActivity = fragmentActivity;
        if (stocks == null) {
            this.stocks = User.currentUser.watchlist;
        } else {
            this.stocks = stocks;
        }
    }

    @Override
    public Fragment getItem(int position) {
        DetailFragment detailFragment = DetailFragment.newInstance(stocks.get(position));
        detailFragment.fragmentActivity = fragmentActivity;
        detailFragment.setMyOnScrollingListener(new DetailFragment.MyOnScrollingListener() {
            @Override
            public void scrollingDown() {
                myScrollingDown();
            }

            @Override
            public void scrollingUp() {
                myScrollingUp();
            }
        });
        return detailFragment;
    }

    @Override
    public int getCount() {
        return stocks.size();
    }

    public void onClickBuyBtn(ViewPager viewPager) {
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if(obj instanceof DetailFragment) {
            ((DetailFragment) obj).onBuy();
        }
    }

    public void onClickSellBtn(ViewPager viewPager) {
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if(obj instanceof DetailFragment) {
            ((DetailFragment) obj).onSell();
        }
    }

    public String getCurrentStockSymbol(ViewPager viewPager) {
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if(obj instanceof DetailFragment) {
            return ((DetailFragment) obj).getSymbol();
        }
        return null;
    }

    protected abstract void myScrollingDown();
    protected abstract void myScrollingUp();
}