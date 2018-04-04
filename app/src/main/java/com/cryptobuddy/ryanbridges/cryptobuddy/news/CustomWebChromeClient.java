package com.cryptobuddy.ryanbridges.cryptobuddy.news;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Ryan on 8/17/2017.
 */


public class CustomWebChromeClient extends WebChromeClient {
    private ProgressListener mListener;

    public CustomWebChromeClient(ProgressListener listener) {
        mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    public interface ProgressListener {
        void onUpdateProgress(int progressValue);
    }
}