package com.fantasystock.fantasystock.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by wilsonsu on 3/22/16.
 */

public class TradeFragment extends DialogFragment {
    private int colorBuy;
    private int colorSell;

    @Bind(R.id.btnTrade) Button btnTrade;
    @Bind(R.id.tvSymbol) TextView tvSymbol;
    @Bind(R.id.tvUnitPrice) TextView tvUnitPrice;
    @Bind(R.id.tvTotalCost) TextView tvTotalCost;
    @Bind(R.id.tvAvailableFund) TextView tvAvailableFund;
    @Bind(R.id.tvEstimatedCost) TextView tvEstimatedCost;
    @Bind(R.id.tvWarning) TextView tvWarning;
    @Bind(R.id.etShares) EditText etShares;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;


    private String symbol;
    private boolean isBuy;
    private DataCenter dataCenter;
    private DecimalFormat formatter;
    private Stock stock;

    public interface TradeFragmentListener {
        void onFinishTrading();
    }


    public static TradeFragment newInstance(String symbol, boolean isBuy) {
        TradeFragment frag = new TradeFragment();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        args.putBoolean("isbuy", isBuy);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade, container);
        ButterKnife.bind(this, view);
        prLoadingSpinner.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch data
        symbol = getArguments().getString("symbol");
        isBuy = getArguments().getBoolean("isbuy");
        dataCenter = DataCenter.getInstance();
        colorBuy = getResources().getColor(R.color.darkBlue);
        colorSell = getResources().getColor(R.color.green);

        stock = User.currentUser.investingStocksMap.get(symbol);
        if(stock == null) {
            stock = DataCenter.getInstance().stockMap.get(symbol);
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setLayout(400, 500);

        tvSymbol.setText("Shares of " + symbol);
        tvEstimatedCost.setText("Estimated "+(isBuy?"Cost":"Gain"));
        btnTrade.setText(isBuy ? "Buy" : "Sell");
        btnTrade.setBackgroundColor(isBuy? colorBuy : colorSell);

        formatter = new DecimalFormat("$###,##0.00");

        DataClient.getInstance().getStockPrice(symbol, new CallBack() {
            @Override
            public void stockCallBack(Stock returnStock) {
                tvUnitPrice.setText(formatter.format(returnStock.current_price));
                if (stock == null) {
                    stock = returnStock;
                } else {
                    stock.current_price = returnStock.current_price;
                }
                onSharesChange();
            }
        });
        etShares.setText("");
        etShares.requestFocus();
    }


    @OnTextChanged(R.id.etShares)
    public void onSharesChange() {
        if (stock == null) return;
        int numShares = 0;
        try {
            numShares = Integer.parseInt(etShares.getText().toString());
        } catch (NumberFormatException e) {
            numShares = 0;
        }

        if (isBuy) {
            tvAvailableFund.setText(formatter.format(User.currentUser.availableFund) + " available");
        } else {
            tvAvailableFund.setText(stock.share + " shares available");
        }

        tvTotalCost.setText(formatter.format(numShares * stock.current_price));
    }

    @OnClick(R.id.btnTrade)
    public void onTrade() {
        int numShares = 0;
        try {
            numShares = Integer.parseInt(etShares.getText().toString());
        } catch (NumberFormatException e) {
            onDismissTrading();
        }

        float cost = numShares * stock.current_price;
        String tradingMessage;
        if (isBuy) {
            if (cost > User.currentUser.availableFund) {
                tvWarning.setText("Not enough funds to buy.");
                return;
            }
            tradingMessage = "Bought " + numShares + " shares of " + symbol + " for " + formatter.format(cost);
        } else {
            if (stock.share < numShares) {
                tvWarning.setText("Not enough shares to sell.");
                return;
            }
            tradingMessage =  "Sold " + numShares + " shares of " + symbol + " for " + formatter.format(cost);
        }
        final String message = tradingMessage;
        if (!isBuy) numShares = -numShares;
        prLoadingSpinner.setVisibility(View.VISIBLE);
        DataCenter.getInstance().trade(stock.symbol, numShares, new CallBack() {
            @Override
            public void done() {
                prLoadingSpinner.setVisibility(View.INVISIBLE);
                Snackbar snackbar = Snackbar.make(getActivity().getCurrentFocus(), message, Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                int colorSnackbar = (isBuy)? colorBuy : colorSell;
                textView.setTextColor(colorSnackbar);
                snackbar.show();
                onDismissTrading();
            }
        });
    }

    private void onDismissTrading() {
        TradeFragmentListener listener = (TradeFragmentListener) getTargetFragment();
        listener.onFinishTrading();
        dismiss();
    }
}