package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rybridges on 2/27/18.
 */

public class CMCQuickSearch {
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("id")
    private int id = -1;

    public String getSlug() {
        return slug;
    }

    public int getId() {
        return id;
    }
}
