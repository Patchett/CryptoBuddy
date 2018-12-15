package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Ryan on 1/16/2018.
 */

public class CMCCoin implements Parcelable {

    public CMCCoin() {

    }

    @JsonProperty("data")
    List<CMCCoinProData> coinsList;

    public CMCCoin(Parcel in){
        String[] data = new String[16];

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
        this.last_updated = data[14];
        this.quickSearchID = Integer.parseInt(data[15]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CMCCoin> CREATOR = new Parcelable.Creator<CMCCoin>() {
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
                this.last_updated,
                Integer.toString(this.quickSearchID)});
    }

    public class CMCCoinProData {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("symbol")
        private String symbol;
        @JsonProperty("slug")
        private String slug;
        @JsonProperty("circulating_supply")
        private long circulating_supply;
        @JsonProperty("total_supply")
        private long total_supply;
        @JsonProperty("max_supply")
        private long max_supply;
        @JsonProperty("cmc_rank")
        private long cmc_rank;
        @JsonProperty("quote")
        private CMCQuote quote;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public long getCirculating_supply() {
            return circulating_supply;
        }

        public void setCirculating_supply(long circulating_supply) {
            this.circulating_supply = circulating_supply;
        }

        public long getTotal_supply() {
            return total_supply;
        }

        public void setTotal_supply(long total_supply) {
            this.total_supply = total_supply;
        }

        public long getMax_supply() {
            return max_supply;
        }

        public void setMax_supply(long max_supply) {
            this.max_supply = max_supply;
        }

        public long getCmc_rank() {
            return cmc_rank;
        }

        public void setCmc_rank(long cmc_rank) {
            this.cmc_rank = cmc_rank;
        }

        public CMCQuote getQuote() {
            return quote;
        }

        public void setQuote(CMCQuote quote) {
            this.quote = quote;
        }
    }

    public class CMCQuote {
        @JsonProperty("price")
        private double price;
        @JsonProperty("volume_24h")
        private double volume_24h;
        @JsonProperty("percent_change_1h")
        private double percent_change_1h;
        @JsonProperty("percent_change_24h")
        private double percent_change_24h;
        @JsonProperty("percent_change_7d")
        private double percent_change_7d;
        @JsonProperty("market_cap")
        private double market_cap;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getVolume_24h() {
            return volume_24h;
        }

        public void setVolume_24h(double volume_24h) {
            this.volume_24h = volume_24h;
        }

        public double getPercent_change_1h() {
            return percent_change_1h;
        }

        public void setPercent_change_1h(double percent_change_1h) {
            this.percent_change_1h = percent_change_1h;
        }

        public double getPercent_change_24h() {
            return percent_change_24h;
        }

        public void setPercent_change_24h(double percent_change_24h) {
            this.percent_change_24h = percent_change_24h;
        }

        public double getPercent_change_7d() {
            return percent_change_7d;
        }

        public void setPercent_change_7d(double percent_change_7d) {
            this.percent_change_7d = percent_change_7d;
        }

        public double getMarket_cap() {
            return market_cap;
        }

        public void setMarket_cap(double market_cap) {
            this.market_cap = market_cap;
        }
    }
}
