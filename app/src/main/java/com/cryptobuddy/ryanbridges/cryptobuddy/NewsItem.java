package com.cryptobuddy.ryanbridges.cryptobuddy;

/**
 * Created by Ryan on 8/13/2017.
 */

public class NewsItem {

    public String articleTitle;
    public String articleURL;
    public String articleBody;
    public String imageURL;
    public String sourceName;
    public long publishedOn;

    public NewsItem(String articleTitle, String articleURL, String articleBody, String imageURL, String sourceName, long publishedOn) {
        this.articleTitle = articleTitle;
        this.articleURL = articleURL;
        this.articleBody = articleBody;
        this.imageURL = imageURL;
        this.sourceName = sourceName;
        this.publishedOn = publishedOn;
    }
}
