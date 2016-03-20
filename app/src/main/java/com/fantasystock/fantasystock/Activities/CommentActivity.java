package com.fantasystock.fantasystock.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fantasystock.fantasystock.Fragments.CommentsFragment;
import com.fantasystock.fantasystock.Fragments.CommentsFullPageFragment;
import com.fantasystock.fantasystock.R;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        String symbol = getIntent().getStringExtra("symbol");
        CommentsFullPageFragment commentsFragment = CommentsFullPageFragment.newInstance(symbol);
        getSupportFragmentManager().beginTransaction().replace(R.id.flComments, commentsFragment).commit();
    }
}
