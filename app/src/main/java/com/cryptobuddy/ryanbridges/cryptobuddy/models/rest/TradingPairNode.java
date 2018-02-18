package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ryan on 2/17/2018.
 */

public class TradingPairNode {

    @JsonProperty("exchange")
    private String exchange;
    @JsonProperty("fromSymbol")
    private String fromSymbol;
    @JsonProperty("toSymbol")
    private String toSymbol;
    @JsonProperty("volume24h")
    private String volume24h;
    @JsonProperty("volume24hTo")
    private String volume24hTo;

    public String getExchange() {
        return exchange;
    }

    public String getFromSymbol() {
        return fromSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public String getVolume24h() {
        return volume24h;
    }

    public String getVolume24hTo() {
        return volume24hTo;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setFromSymbol(String fromSymbol) {
        this.fromSymbol = fromSymbol;
    }

    public void setToSymbol(String toSymbol) {
        this.toSymbol = toSymbol;
    }

    public void setVolume24h(String volume24h) {
        this.volume24h = volume24h;
    }

    public void setVolume24hTo(String volume24hTo) {
        this.volume24hTo = volume24hTo;
    }
}
