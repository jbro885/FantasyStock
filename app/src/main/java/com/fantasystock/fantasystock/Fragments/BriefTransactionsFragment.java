package com.fantasystock.fantasystock.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Models.Transaction;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/21/16.
 */
public class BriefTransactionsFragment extends TransactionsFragment {
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;

    private String symbol;
    public static BriefTransactionsFragment newInstance(String symbol) {
        BriefTransactionsFragment fragment = new BriefTransactionsFragment();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getArguments().getString("symbol");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brief_transactions, container, false);
        ButterKnife.bind(this, view);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryTransactions(symbol);
    }

    @Override
    public void queryTransactions(String symbol) {
        prLoadingSpinner.setVisibility(View.VISIBLE);
        rvList.setMinimumHeight(20);
        DataCenter.getInstance().getTransactions(symbol, new CallBack() {
            @Override
            public void transactionsCallBack(ArrayList<Transaction> returnTransactions) {
                transactions.clear();
                transactions.addAll(returnTransactions);
                adapter.notifyDataSetChanged();
                rvList.setMinimumHeight(0);
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }
        });
    }
}
