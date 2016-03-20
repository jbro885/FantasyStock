package com.fantasystock.fantasystock.Activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebNewsActivity extends AppCompatActivity {
    @Bind(R.id.wvArticle) WebView wvArticle;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_news);

        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");

        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                prLoadingSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }
        });
        wvArticle.loadUrl(url);
    }
}
