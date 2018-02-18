package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ryan on 2/18/2018.
 */

public class MarketsResponse {

    @JsonProperty("Data")
    private ExchangeResponseDataNode data;
    @JsonProperty("Response")
    private String response;

    public ExchangeResponseDataNode getData() {
        return data;
    }

}
