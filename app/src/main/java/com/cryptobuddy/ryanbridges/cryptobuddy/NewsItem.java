package com.cryptobuddy.ryanbridges.cryptobuddy;

/**
 * Created by Ryan on 8/13/2017.
 */

public class NewsItem {

    public String articleTitle;
    public String articleURL;
    public String articleBody;

    public NewsItem(String articleTitle, String articleURL, String articleBody) {
        this.articleTitle = articleTitle;
        this.articleURL = articleURL;
        this.articleBody = articleBody;
    }
}
