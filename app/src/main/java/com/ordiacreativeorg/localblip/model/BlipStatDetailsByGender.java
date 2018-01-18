package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 12/27/2015
 */
public class BlipStatDetailsByGender extends BlipStatDetails implements Serializable {

    @SerializedName("gender")
    @Expose
    private String gender;

    public BlipStatDetailsByGender(String gender, int views, int blipBook, int redeemed) {
        super(views, blipBook, redeemed);
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
