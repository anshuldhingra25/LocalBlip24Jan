package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class BlipBoost implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer boostId;

    @SerializedName(value="title", alternate={"productname"})
    @Expose
    private String title;

    @SerializedName("Summary")
    @Expose
    private String summary;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName(value="url", alternate={"producturl"})
    @Expose
    private String url;

    @SerializedName("image")
    @Expose
    private String image;

    public Integer getBoostId() {
        return boostId;
    }

    public void setBoostId(Integer boostId) {
        this.boostId = boostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
