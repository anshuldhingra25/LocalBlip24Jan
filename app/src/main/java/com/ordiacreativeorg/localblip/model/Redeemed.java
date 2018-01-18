package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class Redeemed implements Serializable{

    @SerializedName(value="coupontitle", alternate={"title", "Bliptitle"})
    @Expose
    private String couponTitle;

    @SerializedName(value="value", alternate={"blipvalue"})
    @Expose
    private int value;

    @SerializedName(value="valuetype", alternate={"blipvaluetype"})
    @Expose
    private int valueType;

    @SerializedName("expiredate")
    @Expose
    private String expireDate;

    @SerializedName(value="vendorname", alternate={"vendor", "vendortitle"})
    @Expose
    private String vendorName;

    @SerializedName("redeem")
    @Expose
    private int redeem;

    @SerializedName("response")
    @Expose
    private String response;

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public boolean isRedeemed() {
        return redeem == 1;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeem = ( redeemed ? 1 : 0 );
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}