package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fantasystock.fantasystock.Fragments.CommentsFullPageFragment;
import com.fantasystock.fantasystock.Fragments.TransactionsFragment;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SeeMoreActivity extends AppCompatActivity {
    @Bind(R.id.tvMenuTitle) TextView tvMenuTitle;
    @Bind(R.id.tvMenuSubTitle) TextView tvMenuSubTitle;
    public static Intent newIntent(Context context, String symbol, boolean isComment) {
        Intent intent = new Intent(context, SeeMoreActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("isComment", isComment);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        String symbol = getIntent().getStringExtra("symbol");
        boolean isComment = getIntent().getBooleanExtra("isComment", true);
        tvMenuTitle.setText(symbol);
        tvMenuSubTitle.setText(isComment ? "Comments" : "History");

        Fragment fragmentPlaceHolder;
        if (isComment) {
            fragmentPlaceHolder = CommentsFullPageFragment.newInstance(symbol);

        } else {
            fragmentPlaceHolder = TransactionsFragment.newInstance(symbol);


        }
        getSupportFragmentManager().beginTransaction().replace(R.id.flComments, fragmentPlaceHolder).commit();

    }
}
