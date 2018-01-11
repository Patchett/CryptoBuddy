package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fco on 10-01-18.
 */

public class DataNode {

    @JsonProperty("Id")
    private String Id;
    @JsonProperty("Url")
    private String Url;
    @JsonProperty("ImageUrl")
    private String ImageUrl;
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Symbol")
    private String Symbol;
    @JsonProperty("CoinName")
    private String CoinName;
    @JsonProperty("FullName")
    private String FullName;
    @JsonProperty("Algorithm")
    private String Algorithm;
    @JsonProperty("ProofType")
    private String ProofType;
    @JsonProperty("FullyPremined")
    private String FullyPremined;
    @JsonProperty("TotalCoinSupply")
    private String TotalCoinSupply;
    @JsonProperty("PreminedValue")
    private String PreMinedValue;
    @JsonProperty("TotalCoinsFreeFloat")
    private String TotalCoinsFreeFloat;
    @JsonProperty("SortOrder")
    private String SortOrder;
    @JsonProperty("Sponsored")
    private boolean Sponsored;

    public String getId() {
        return Id;
    }

    public DataNode setId(String id) {
        Id = id;
        return this;
    }

    public String getUrl() {
        return Url;
    }

    public DataNode setUrl(String url) {
        Url = url;
        return this;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public DataNode setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
        return this;
    }

    public String getName() {
        return Name;
    }

    public DataNode setName(String name) {
        Name = name;
        return this;
    }

    public String getSymbol() {
        return Symbol;
    }

    public DataNode setSymbol(String symbol) {
        Symbol = symbol;
        return this;
    }

    public String getCoinName() {
        return CoinName;
    }

    public DataNode setCoinName(String coinName) {
        CoinName = coinName;
        return this;
    }

    public String getFullName() {
        return FullName;
    }

    public DataNode setFullName(String fullName) {
        FullName = fullName;
        return this;
    }

    public String getAlgorithm() {
        return Algorithm;
    }

    public DataNode setAlgorithm(String algorithm) {
        Algorithm = algorithm;
        return this;
    }

    public String getProofType() {
        return ProofType;
    }

    public DataNode setProofType(String proofType) {
        ProofType = proofType;
        return this;
    }

    public String getFullyPremined() {
        return FullyPremined;
    }

    public DataNode setFullyPremined(String fullyPremined) {
        FullyPremined = fullyPremined;
        return this;
    }

    public String getTotalCoinSupply() {
        return TotalCoinSupply;
    }

    public DataNode setTotalCoinSupply(String totalCoinSupply) {
        TotalCoinSupply = totalCoinSupply;
        return this;
    }

    public String getPreMinedValue() {
        return PreMinedValue;
    }

    public DataNode setPreMinedValue(String preMinedValue) {
        PreMinedValue = preMinedValue;
        return this;
    }

    public String getTotalCoinsFreeFloat() {
        return TotalCoinsFreeFloat;
    }

    public DataNode setTotalCoinsFreeFloat(String totalCoinsFreeFloat) {
        TotalCoinsFreeFloat = totalCoinsFreeFloat;
        return this;
    }

    public String getSortOrder() {
        return SortOrder;
    }

    public DataNode setSortOrder(String sortOrder) {
        SortOrder = sortOrder;
        return this;
    }

    public boolean isSponsored() {
        return Sponsored;
    }

    public DataNode setSponsored(boolean sponsored) {
        Sponsored = sponsored;
        return this;
    }
}
