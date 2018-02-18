package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ryan on 2/18/2018.
 */

public class MarketNode {
    @JsonProperty("MARKET")
    private String market;
    @JsonProperty("FROMSYMBOL")
    private String fromSymbol;
    @JsonProperty("TOSYMBOL")
    private String toSymbol;
    @JsonProperty("PRICE")
    private float price;
    @JsonProperty("VOLUME24HOUR")
    private float volume24h;
    @JsonProperty("CHANGEPCT24HOUR")
    private float changePct24h;
    @JsonProperty("CHANGE24HOUR")
    private float change24h;

    public String getMarket() {
        return market;
    }

    public String getFromSymbol() {
        return fromSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public float getPrice() {
        return price;
    }

    public float getVolume24h() {
        return volume24h;
    }

    public float getChangePct24h() {
        return changePct24h;
    }

    public float getChange24h() {
        return change24h;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setFromSymbol(String fromSymbol) {
        this.fromSymbol = fromSymbol;
    }

    public void setToSymbol(String toSymbol) {
        this.toSymbol = toSymbol;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setVolume24h(float volume24h) {
        this.volume24h = volume24h;
    }

    public void setChangePct24h(float changePct24h) {
        this.changePct24h = changePct24h;
    }

    public void setChange24h(float change24h) {
        this.change24h = change24h;
    }
}
