package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 12/27/2015
 */
public class BlipStatDetailsByAge extends BlipStatDetails implements Serializable {

    @SerializedName("age")
    @Expose
    private String age;

    public String getAge() {
        return age;
    }

    public BlipStatDetailsByAge(String age, int views, int blipBook, int redeemed) {
        super(views, blipBook, redeemed);
        this.age = age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
