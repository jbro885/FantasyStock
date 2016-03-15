package com.fantasystock.fantasystock.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.Transaction;
import com.fantasystock.fantasystock.R;
import com.fantasystock.fantasystock.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/13/16.
 */
public class TransactionsFragment extends Fragment{
    private ArrayList<Transaction> transactions;
    private TransactionsArrayAdapter adapter;
    @Bind(R.id.rvList) RecyclerView rvList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactions = new ArrayList<>();
        adapter = new TransactionsArrayAdapter(transactions);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        ButterKnife.bind(this, view);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void queryTransactions(String symbol) {
        DataCenter.getInstance().getTransactions(symbol, new CallBack() {
            @Override
            public void transactionsCallBack(ArrayList<Transaction> returnTransactions) {
                transactions.clear();
                transactions.addAll(returnTransactions);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static class TransactionsArrayAdapter extends RecyclerView.Adapter<TransactionsViewHolder> {
        private ArrayList<Transaction> transactions;

        public TransactionsArrayAdapter(ArrayList<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public TransactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_transaction, parent, false);
            return new TransactionsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionsViewHolder holder, int position) {
            holder.setTransaction(transactions.get(position));
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }
    }
    public static class TransactionsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvQuoteSymbol)
        TextView tvQuoteSymbol;
        @Bind(R.id.tvTransactionDate) TextView tvTransactionDate;
        @Bind(R.id.tvEquityValue) TextView tvEquityValue;
        @Bind(R.id.tvShares) TextView tvShares;

        public TransactionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void setTransaction(Transaction transaction) {
            String buyText = (transaction.data.shares>0?"BUY":"SELL");
            tvQuoteSymbol.setText(buyText + " " + transaction.data.symbol);
            tvEquityValue.setText(Utils.moneyConverter(transaction.data.shares * transaction.data.avgPrice));
            DateFormat df = new SimpleDateFormat("HH:mm MM/dd/yyyy");
            tvTransactionDate.setText(df.format(transaction.updatedAt));
            tvShares.setText(Math.abs(transaction.data.shares) + " shares at $" + Utils.moneyConverter(transaction.data.avgPrice));
        }
    }
}
