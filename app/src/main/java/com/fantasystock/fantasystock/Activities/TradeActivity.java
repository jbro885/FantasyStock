package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasystock.fantasystock.CallBack;
import com.fantasystock.fantasystock.DataCenter;
import com.fantasystock.fantasystock.DataClient;
import com.fantasystock.fantasystock.Models.Stock;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TradeActivity extends AppCompatActivity {

  @Bind(R.id.btnTrade) Button btnTrade;
  @Bind(R.id.tvSymbol) TextView tvSymbol;
  @Bind(R.id.tvUnitPrice) TextView tvUnitPrice;
  @Bind(R.id.tvTotalCost) TextView tvTotalCost;
  @Bind(R.id.tvEstimatedCost) TextView tvEstimatedCost;
  @Bind(R.id.etShares) EditText etShares;

  private String symbol;
  private String buySell;
  private DataCenter dataCenter;
  private DecimalFormat formatter;
  private Stock stock;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trade);
    ButterKnife.bind(this);

    Intent intent = getIntent();
    symbol = intent.getStringExtra("symbol");
    buySell = intent.getStringExtra("buySell");
    tvSymbol.setText("Shares of " + symbol);
    if (buySell.equals("buy")) {
      tvEstimatedCost.setText("Estimated Cost");
    } else {
      tvEstimatedCost.setText("Estimated Gain");
    }
    btnTrade.setText(buySell);

    dataCenter = DataCenter.getInstance();
    stock = (Stock) User.currentUser.investingStocksMap.get(symbol);
    formatter = new DecimalFormat("$###,##0.00");
      DataClient.getInstance().getStockPrice(symbol, new CallBack() {
          @Override
          public void stockCallBack(Stock returnStock) {
              tvUnitPrice.setText(formatter.format(returnStock.current_price));
              if (stock==null) {
                  stock = returnStock;
              } else {
                  stock.current_price = returnStock.current_price;
              }


          }
      });
    etShares.setText("");
    etShares.requestFocus();
    // TODO: input method is not default out :(
  }

  @OnTextChanged(R.id.etShares)
  public void onSharesChange() {
    int numShares = 0;
    try {
      numShares = Integer.parseInt(etShares.getText().toString());
    } catch (NumberFormatException e) {
      numShares = 0;
    }
    if (numShares == 0) {
      if (buySell.equals("buy")) {
        tvTotalCost.setText(formatter.format(User.currentUser.availableFund) + " available");
      } else {
        tvTotalCost.setText(stock.share + " shares available");
      }
    } else {
      tvTotalCost.setText(formatter.format(numShares * stock.current_price));
    }
  }

  @OnClick(R.id.btnTrade)
  public void onTrade() {
      int numShares = 0;
      try {
        numShares = Integer.parseInt(etShares.getText().toString());
      } catch (NumberFormatException e) {
          finish();
      }

      float cost = numShares * stock.current_price;
      if (buySell.equals("buy")) {
        if (cost > User.currentUser.availableFund) {
          Toast.makeText(this, "Not enough funds to buy", Toast.LENGTH_LONG).show();
          return;
        }
        Toast.makeText(this, "Bought " + numShares + " shares of " + symbol + " for " + formatter.format(cost), Toast.LENGTH_LONG).show();

      } else {
        if (stock.share < numShares) {
          Toast.makeText(this, "Not enough shares to sell.", Toast.LENGTH_LONG).show();
          return;
        }
        Toast.makeText(this, "Sold " + numShares + " shares of " + symbol + " for " + formatter.format(cost), Toast.LENGTH_LONG).show();
      }
      if (!buySell.equals("buy")) numShares = -numShares;
      DataCenter.getInstance().trade(stock.symbol, numShares, new CallBack() {
          @Override
          public void done() {
              finish();
          }
      });

  }
}
