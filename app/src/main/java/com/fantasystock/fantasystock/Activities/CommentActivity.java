package com.fantasystock.fantasystock.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fantasystock.fantasystock.Fragments.CommentsFragment;
import com.fantasystock.fantasystock.Fragments.CommentsFullPageFragment;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentActivity extends AppCompatActivity {
    @Bind(R.id.tvMenuTitle) TextView tvMenuTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        String symbol = getIntent().getStringExtra("symbol");
        tvMenuTitle.setText(symbol);
        CommentsFullPageFragment commentsFragment = CommentsFullPageFragment.newInstance(symbol);
        getSupportFragmentManager().beginTransaction().replace(R.id.flComments, commentsFragment).commit();
    }
}
