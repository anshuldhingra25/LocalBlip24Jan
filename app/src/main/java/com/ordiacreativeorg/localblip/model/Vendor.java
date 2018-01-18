package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class Vendor {

    @SerializedName("businessname")
    @Expose
    private String businessName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("memberid")
    @Expose
    private int memberId;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
