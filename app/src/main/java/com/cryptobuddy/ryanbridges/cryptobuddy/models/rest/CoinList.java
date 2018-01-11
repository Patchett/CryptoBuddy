package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fco on 09-01-18.
 */

/**
 * Main serialization class for the coin list.
 */
public class CoinList {

    /**
     * Class for serializing the Data key in the JSON. Since the objects inside the response
     * have mutable keys, we will use the JsonAnySetter annotation to tell the object mapper to
     * "send any JSON that matches the following format through here". While usually AnySetter is
     * used to send key value pairs towards a map, we will use a plain, serialized list, since
     * all the objects inside the Data object are identical. The class does not need to be nested,
     * all of them can be plain public classes or nested classes.
     */
    public static class Data {
        private List<DataNode> dataList;

        public List<DataNode> getDataList() {
            return dataList;
        }

        @JsonAnySetter
        public void setDataList(String key, DataNode dataNode) {
            if(dataList == null) dataList = new ArrayList<>();
            dataList.add(dataNode);
        }
    }

    /**
     * Regular JSON members.
     */
    @JsonProperty("Response")
    private String Response;
    @JsonProperty("Message")
    private String Message;
    @JsonProperty("BaseImageUrl")
    private String BaseImageUrl;
    @JsonProperty("BaseLinkUrl")
    private String BaseLinkUrl;
    @JsonProperty("DefaultWatchlist")
    private WatchList DefaultWatchlist;
    @JsonProperty("Data")
    private Data data;
    @JsonProperty("Type")
    private int Type;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getBaseImageUrl() {
        return BaseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        BaseImageUrl = baseImageUrl;
    }

    public String getBaseLinkUrl() {
        return BaseLinkUrl;
    }

    public void setBaseLinkUrl(String baseLinkUrl) {
        BaseLinkUrl = baseLinkUrl;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public WatchList getDefaultWatchlist() {
        return DefaultWatchlist;
    }

    public void setDefaultWatchlist(WatchList defaultWatchlist) {
        DefaultWatchlist = defaultWatchlist;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}




