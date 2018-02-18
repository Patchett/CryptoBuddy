package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Ryan on 2/17/2018.
 */

public class TradingPair {

    @JsonProperty("Data")
    private List<TradingPairNode> data;
    @JsonProperty("Response")
    private String response;

    public List<TradingPairNode> getData() {
        return data;
    }

    public String getResponse() {
        return response;
    }

    public void setData(List<TradingPairNode> data) {
        this.data = data;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
