package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fco on 18-01-18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "guid",
        "published_on",
        "imageurl",
        "title",
        "url",
        "source",
        "body",
        "tags",
        "lang",
        "source_info"
})
public class News {
    @JsonProperty("id")
    private String id;
    @JsonProperty("guid")
    private String guid;
    @JsonProperty("published_on")
    private Integer publishedOn;
    @JsonProperty("imageurl")
    private String imageurl;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("source")
    private String source;
    @JsonProperty("body")
    private String body;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("lang")
    private String lang;
    @JsonProperty("source_info")
    private SourceInfo sourceInfo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public News setId(String id) {
        this.id = id;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public News setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public Integer getPublishedOn() {
        return publishedOn;
    }

    public News setPublishedOn(Integer publishedOn) {
        this.publishedOn = publishedOn;
        return this;
    }

    public String getImageurl() {
        return imageurl;
    }

    public News setImageurl(String imageurl) {
        this.imageurl = imageurl;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public News setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public News setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSource() {
        return source;
    }

    public News setSource(String source) {
        this.source = source;
        return this;
    }

    public String getBody() {
        return body;
    }

    public News setBody(String body) {
        this.body = body;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public News setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public News setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public News setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public News setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
