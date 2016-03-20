package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.News;
import com.fantasystock.fantasystock.R;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 3/12/16.
 */
public class NewsActivity extends AppCompatActivity {
    private News news;

    @Bind(R.id.tvTitle) TextView tvTitle;
    @Bind(R.id.tvPublished) TextView tvPublished;
    @Bind(R.id.tvPublisher) TextView tvPublisher;
    @Bind(R.id.ivImage) ImageView ivImage;
    @Bind(R.id.tvContent) TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        String newsString = intent.getStringExtra("newsString");

        news = new Gson().fromJson(newsString, News.class);

        ButterKnife.bind(this);


        String str = Html.fromHtml(news.content).toString();

        tvTitle.setText(news.title);
        tvContent.setText(str);
        tvPublished.setText(Utils.converTimetoRelativeTime(news.published));
        tvPublisher.setText(news.publisher);
        if(news.images != null) {
            Glide.with(getApplicationContext())
                    .load(news.images.get(0).url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImage);
        }
    }

    @Override
    public void onBackPressed(){
        supportFinishAfterTransition();
    }


}
