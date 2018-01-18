package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class BlipStatDetails implements Serializable{

    @SerializedName("views")
    @Expose
    private int views;

    @SerializedName("blipbook")
    @Expose
    private int blipBook;

    @SerializedName("redeemed")
    @Expose
    private int redeemed;

    public BlipStatDetails(int views, int blipBook, int redeemed) {
        this.views = views;
        this.blipBook = blipBook;
        this.redeemed = redeemed;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getBlipBook() {
        return blipBook;
    }

    public void setBlipBook(int blipBook) {
        this.blipBook = blipBook;
    }

    public int getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(int redeemed) {
        this.redeemed = redeemed;
    }
}
