package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {
    private DataClient client;
    private ArrayList<Stock> stocks;
    private SearchQuoteArrayAdapter adapter;
    @Bind(R.id.rvList) RecyclerView rvList;
    @Bind(R.id.etSearchQuote) EditText etSearchQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setup();
    }

    private void setup() {
        client = DataClient.getInstance();
        stocks = DataCenter.getInstance().allFavoritedStocks();
        adapter = new SearchQuoteArrayAdapter(stocks);
        etSearchQuote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryText = etSearchQuote.getText().toString();
                if (queryText.length()==0) {
                    stocks.clear();
                    stocks.addAll(DataCenter.getInstance().allFavoritedStocks());
                    adapter.notifyDataSetChanged();
                    return;
                }
                client.searchQuote(queryText, new CallBack(){
                    @Override
                    public void stocksCallBack(ArrayList<Stock> responseStocks) {
                        handleSearchResults(responseStocks);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

    }

    private void handleSearchResults(ArrayList<Stock> responseStocks) {
//        stocks.clear();
//        stocks.addAll(responseStocks);
        client.getStocksPrices(responseStocks, new CallBack() {
            @Override
            public void stocksCallBack(ArrayList<Stock> responseStocks) {
                stocks.clear();
                stocks.addAll(responseStocks);
                adapter.notifyDataSetChanged();
            }
        });
//        adapter.notifyDataSetChanged();
    }


    private static class SearchQuoteArrayAdapter extends RecyclerView.Adapter<SearchQuoteViewHolder> {
        private ArrayList<Stock> stocks;

        public SearchQuoteArrayAdapter(ArrayList<Stock> stocks) {
            this.stocks = stocks;
        }

        @Override
        public SearchQuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_search_quote, parent, false);
            return new SearchQuoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchQuoteViewHolder holder, int position) {
            holder.setStock(stocks.get(position));

        }

        @Override
        public int getItemCount() {
            return stocks.size();
        }
    }

    public static class SearchQuoteViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvQuoteSymbol) TextView tvQuoteSymbol;
        @Bind(R.id.tvQuoteName) TextView tvQuoteName;
        @Bind(R.id.ibFavorite) ImageButton ibFavorite;
        @Bind(R.id.tvQuoteMarket) TextView tvQuoteMarket;
        private boolean isFavorited;
        private Stock stock;

        public SearchQuoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void setStock(Stock stock) {
            this.stock = stock;
            tvQuoteSymbol.setText(stock.symbol);
            tvQuoteName.setText(stock.name);
            if (stock.current_price>0) {
                tvQuoteMarket.setText(Float.toString(stock.current_price));
            } else {
                tvQuoteMarket.setText(stock.market);
            }
            isFavorited = DataCenter.getInstance().isFavoritedStock(stock);
            reloadFavorite();

        }
        @OnClick(R.id.ibFavorite)
        public void onFavoriteOnClick() {
            if (isFavorited) {
                DataCenter.getInstance().unfavoriteStock(stock);
            } else {
                DataCenter.getInstance().favoriteStock(stock);
            }
            reloadFavorite();
        }
        private void reloadFavorite() {
            isFavorited = DataCenter.getInstance().isFavoritedStock(stock);
            ibFavorite.setImageResource(
                    isFavorited ? R.drawable.ic_star : R.drawable.ic_unstar
            );

        }
    }
}
