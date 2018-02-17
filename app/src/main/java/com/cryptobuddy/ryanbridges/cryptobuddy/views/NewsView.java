package com.cryptobuddy.ryanbridges.cryptobuddy.views;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.news.NewsItem;
import com.cryptobuddy.ryanbridges.cryptobuddy.news.WebViewActivity;
import com.squareup.picasso.Picasso;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

/**
 * Created by fco on 29-01-18.
 */

public class NewsView extends ConstraintLayout {
    
    private TextView articleTitleTextView;
    private ImageView articleImageView;
    private TextView ageTextView;
    private TextView sourceNameTextView;
    
    private NewsItem newsItem;
    
    public NewsView(Context context) {
        super(context);
        inflateComponents();
    }

    public NewsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateComponents();
    }

    public NewsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateComponents();
    }
    
    protected void inflateComponents(){
        inflate(getContext(), R.layout.row_news_item, this);
        articleTitleTextView = (TextView) findViewById(R.id.articleTitle);
        ageTextView = (TextView) findViewById(R.id.age);
        articleImageView = (ImageView) findViewById(R.id.articleImage);
        sourceNameTextView = (TextView) findViewById(R.id.sourceName);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(getContext(), WebViewActivity.class);
                browserIntent.putExtra("url", newsItem.articleURL);
                browserIntent.putExtra("title", "News");
                getContext().startActivity(browserIntent);
            }
        });
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.orientation = LayoutParams.VERTICAL;
        setLayoutParams(params);
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
        setFocusable(true);
        setClickable(true);
    }
    
    public NewsView setData(NewsItem newsItem){
        this.newsItem = newsItem;
        if(this.newsItem != null){
            articleTitleTextView.setText(this.newsItem.articleTitle);
            String publishTimeString = (String) DateUtils.getRelativeTimeSpanString((long) this.newsItem.publishedOn * 1000, System.currentTimeMillis(), MINUTE_IN_MILLIS);
            ageTextView.setText(publishTimeString);
            sourceNameTextView.setText(this.newsItem.sourceName);
            Picasso.with(getContext()).load(this.newsItem.imageURL).into(articleImageView);
        }
        return this;
    }
}
