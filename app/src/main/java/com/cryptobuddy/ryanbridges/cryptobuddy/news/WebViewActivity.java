package com.cryptobuddy.ryanbridges.cryptobuddy.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;


import com.cryptobuddy.ryanbridges.cryptobuddy.BaseAnimationActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;

public class WebViewActivity extends BaseAnimationActivity implements CustomWebChromeClient.ProgressListener {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_webview);
        setSupportActionBar(mToolbar);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWebView = (WebView) this.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgressBar.setMax(100);
        mWebView.setWebChromeClient(new CustomWebChromeClient(this));
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);

            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mProgressBar.setProgress(0);
    }

    @Override
    public void onUpdateProgress(int progressValue) {
        mProgressBar.setProgress(progressValue);
        if (progressValue == 100) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
