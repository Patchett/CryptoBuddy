package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ryan on 1/16/2018.
 */

public class CMCCoin implements Parcelable {

    public CMCCoin() {

    }

    public CMCCoin(Parcel in){
        String[] data = new String[15];

        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.symbol = data[2];
        this.rank = data[3];
        this.price_usd = data[4];
        this.price_btc = data[5];
        this.volume_usd_24h = data[6];
        this.market_cap_usd = data[7];
        this.available_supply = data[8];
        this.total_supply = data[9];
        this.max_supply = data[10];
        this.percent_change_1h = data[11];
        this.percent_change_24h = data[12];
        this.percent_change_7d = data[13];
        this.last_updated= data[14];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CMCCoin> CREATOR= new Parcelable.Creator<CMCCoin>() {
        @Override
        public CMCCoin createFromParcel(Parcel source) {
            return new CMCCoin(source);  //using parcelable constructor
        }
        @Override
        public CMCCoin[] newArray(int size) {
            return new CMCCoin[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.id,
                this.name,
                this.symbol,
                this.rank,
                this.price_usd,
                this.price_btc,
                this.volume_usd_24h,
                this.market_cap_usd,
                this.available_supply,
                this.total_supply,
                this.max_supply,
                this.percent_change_1h,
                this.percent_change_24h,
                this.percent_change_7d,
                this.last_updated});
    }

    //Side note: the names don't need to match. The annotation name is the one used to serialize the property.
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("rank")
    private String rank;
    @JsonProperty("price_usd")
    private String price_usd;
    @JsonProperty("price_btc")
    private String price_btc;
    @JsonProperty("24h_volume_usd")
    private String volume_usd_24h;
    @JsonProperty("market_cap_usd")
    private String market_cap_usd;
    @JsonProperty("available_supply")
    private String available_supply;
    @JsonProperty("total_supply")
    private String total_supply;
    @JsonProperty("max_supply")
    private String max_supply;
    @JsonProperty("percent_change_1h")
    private String percent_change_1h;
    @JsonProperty("percent_change_24h")
    private String percent_change_24h;
    @JsonProperty("percent_change_7d")
    private String percent_change_7d;
    @JsonProperty("last_updated")
    private String last_updated;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getRank() {
        return rank;
    }

    public String getPrice_usd() {
        return price_usd;
    }

    public String getPrice_btc() {
        return price_btc;
    }

    public String getVolume_usd_24h() {
        return volume_usd_24h;
    }

    public String getMarket_cap_usd() {
        return market_cap_usd;
    }

    public String getAvailable_supply() {
        return available_supply;
    }

    public String getTotal_supply() {
        return total_supply;
    }

    public String getMax_supply() {
        return max_supply;
    }

    public String getPercent_change_1h() {
        return percent_change_1h;
    }

    public String getPercent_change_24h() {
        return percent_change_24h;
    }

    public String getPercent_change_7d() {
        return percent_change_7d;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setPrice_usd(String price_usd) {
        this.price_usd = price_usd;
    }

    public void setPrice_btc(String price_btc) {
        this.price_btc = price_btc;
    }

    public void setVolume_usd_24h(String volume_usd_24h) {
        this.volume_usd_24h = volume_usd_24h;
    }

    public void setMarket_cap_usd(String market_cap_usd) {
        this.market_cap_usd = market_cap_usd;
    }

    public void setAvailable_supply(String available_supply) {
        this.available_supply = available_supply;
    }

    public void setTotal_supply(String total_supply) {
        this.total_supply = total_supply;
    }

    public void setMax_supply(String max_supply) {
        this.max_supply = max_supply;
    }

    public void setPercent_change_1h(String percent_change_1h) {
        this.percent_change_1h = percent_change_1h;
    }

    public void setPercent_change_24h(String percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }

    public void setPercent_change_7d(String percent_change_7d) {
        this.percent_change_7d = percent_change_7d;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}
