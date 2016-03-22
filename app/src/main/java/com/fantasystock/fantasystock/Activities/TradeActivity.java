package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class TradeActivity extends AppCompatActivity {

  @Bind(R.id.btnTrade) Button btnTrade;
  @Bind(R.id.tvSymbol) TextView tvSymbol;
  @Bind(R.id.tvUnitPrice) TextView tvUnitPrice;
  @Bind(R.id.tvTotalCost) TextView tvTotalCost;
  @Bind(R.id.tvEstimatedCost) TextView tvEstimatedCost;
  @Bind(R.id.etShares) EditText etShares;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trade);
    ButterKnife.bind(this);

  }


}
