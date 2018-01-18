package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 12/27/2015
 */
public class BlipStatDetailsByLocation extends BlipStatDetails implements Serializable {

    @SerializedName("zipcode")
    @Expose
    private String zipcode;

    public BlipStatDetailsByLocation(String zipcode, int views, int blipBook, int redeemed) {
        super(views, blipBook, redeemed);
        this.zipcode = zipcode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
