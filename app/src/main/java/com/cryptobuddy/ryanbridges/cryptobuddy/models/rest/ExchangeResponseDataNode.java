package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Ryan on 2/18/2018.
 */

public class ExchangeResponseDataNode {

    @JsonProperty("Exchanges")
    private List<MarketNode> marketsList;

    public List<MarketNode> getMarketsList() {
        return marketsList;
    }

    public void setMarketsList(List<MarketNode> marketsList) {
        this.marketsList = marketsList;
    }
}
