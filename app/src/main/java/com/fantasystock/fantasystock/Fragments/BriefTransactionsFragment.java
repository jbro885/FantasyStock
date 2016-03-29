package com.fantasystock.fantasystock.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.Activities.SeeMoreActivity;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Models.Transaction;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/21/16.
 */
public class BriefTransactionsFragment extends TransactionsFragment {
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    @Bind(R.id.btnMoreTransactions) Button btnMoreTransactions;

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
    public void queryTransactions(String symbol) {
        toggleLoadingState(true);
        rvList.setMinimumHeight(20);
        DataCenter.getInstance().getTransactions(symbol, new CallBack() {
            @Override
            public void transactionsCallBack(ArrayList<Transaction> returnTransactions) {
                transactions.clear();
                int len = returnTransactions.size();
                for (int i = 0; i < len && i < 5; ++i) {
                    transactions.add(returnTransactions.get(len - 1 - i));
                }
                adapter.notifyDataSetChanged();
                rvList.setMinimumHeight(0);

                toggleLoadingState(false);
            }

            @Override
            public void onFail(String failureMessage) {
                toggleLoadingState(false);
            }
        });
    }

    private void toggleLoadingState(boolean isLoading) {
        if (isLoading) {
            prLoadingSpinner.setVisibility(View.VISIBLE);
            btnMoreTransactions.setVisibility(View.INVISIBLE);
        } else {
            prLoadingSpinner.setVisibility(View.INVISIBLE);
            btnMoreTransactions.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnMoreTransactions)
    public void onSeeMoreClick() {
        Intent intent = SeeMoreActivity.newIntent(getContext(), symbol, false);
        startActivity(intent);
    }


}
