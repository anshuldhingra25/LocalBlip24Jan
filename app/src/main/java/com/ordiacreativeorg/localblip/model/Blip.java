package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class Blip implements Serializable {




    @SerializedName("contenttype")
    @Expose
    private int contenttype;

    @SerializedName("videotype")
    @Expose
    private int videotype;

    @SerializedName("blipvideo")
    @Expose
    private String blipvideo;

    @SerializedName("skyClosest")
    @Expose
    private int skyClosest;

    @SerializedName("vendorAID")
    @Expose
    private int vendorId;

    @SerializedName(value = "couponid", alternate = {"blipid"})
    @Expose
    private int couponId = -1;

    @SerializedName(value = "coupontitle", alternate = {"title", "Bliptitle"})
    @Expose
    private String couponTitle;

    @SerializedName("description")
    @Expose
    private String description;



    @SerializedName("businessdesc")
    @Expose
    private String businessdesc;

    @SerializedName("fineprint")
    @Expose
    private String finePrint;

    @SerializedName(value = "couponurl", alternate = {"url"})
    @Expose
    private String couponUrl;

    @SerializedName(value = "location", alternate = {"locations"})
    @Expose
    private List<Location> locations = new ArrayList<>();

    @SerializedName("category")
    @Expose
    private List<Category> categories = new ArrayList<>();

    @SerializedName("remaining")
    @Expose
    private String remaining;

    @SerializedName(value = "value", alternate = {"blipvalue"})
    @Expose
    private int value;

    @SerializedName(value = "valuetype", alternate = {"blipvaluetype"})
    @Expose
    private int valueType;

    @SerializedName("expiredate")
    @Expose
    private String expireDate;

    @SerializedName("vendormail")
    @Expose
    private String vendorMail;

    @SerializedName(value = "vendorname", alternate = {"vendor"})
    @Expose
    private String vendorName;

    @SerializedName("online")
    @Expose
    private int online;

    @SerializedName("quantity")
    @Expose
    private String quantity;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("publish")
    @Expose
    private int publish;

    @SerializedName("promote")
    @Expose
    private int promote;

    @SerializedName("itsright")
    @Expose
    private String itsRight;

    @SerializedName("bottom")
    @Expose
    private String bottom;

    @SerializedName(value = "blipimage", alternate = {"qrimage"})
    @Expose
    private String blipImage;

    @SerializedName("blipimagesmall")
    @Expose
    private String blipImageSmall;

    @SerializedName("blipspicks")
    @Expose
    private List<Blip> blipsPicks = new ArrayList<>();

    @SerializedName("qr")
    @Expose
    private String qr;

    @SerializedName("printed")
    @Expose
    private int printed;

    @SerializedName("useddate")
    @Expose
    private String usedDate;

    @SerializedName("addedtobb")
    @Expose
    private int addedtobb;

    @SerializedName("myvote")
    @Expose
    private int myVote;

    @SerializedName("positivevotescount")
    @Expose
    private int positiveVotesCount;

    @SerializedName("negativevotescount")
    @Expose
    private int negativeVotesCount;

    @SerializedName("views")
    @Expose
    private Integer views;

    @SerializedName("couponbookcount")
    @Expose
    private Integer couponBookCount;

    @SerializedName("mobileapp")
    @Expose
    private Integer mobileCount;

    @SerializedName("printedcount")
    @Expose
    private Integer printedCount;

    @SerializedName("qrcount")
    @Expose
    private Integer qrCount;

    @SerializedName("franchise")
    @Expose
    private String franchise_owner;

    @SerializedName("social")
    @Expose
    private List<SocialMedia> social = new ArrayList<>();

    //Just for create/save a blip
    private int expDay;
    private int expMonth;
    private int expYear;

    public String getFranchise_owner() {
        return franchise_owner;
    }

    public void setFranchise_owner(String franchise_owner) {
        this.franchise_owner = franchise_owner;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFinePrint() {
        return finePrint;
    }

    public void setFinePrint(String finePrint) {
        this.finePrint = finePrint;
    }

    public String getCouponUrl() {
        return couponUrl;
    }

    public void setCouponUrl(String couponUrl) {
        this.couponUrl = couponUrl;
    }
    public int getContenttype() {
        return contenttype;
    }

    public void setContenttype(int contenttype) {
        this.contenttype = contenttype;
    }
    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
    public String getBusinessdesc() {
        return businessdesc;
    }

    public void setBusinessdesc(String businessdesc) {
        this.businessdesc = businessdesc;
    }
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
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

    public String getVendorMail() {
        return vendorMail;
    }

    public void setVendorMail(String vendorMail) {
        this.vendorMail = vendorMail;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public boolean isOnline() {
        return online > 0;
    }

    public void setOnline(boolean online) {
        this.online = online ? 1 : 0;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status == 1;
    }

    public void setStatus(boolean status) {
        this.status = (status ? 1 : 0);
    }

    public boolean isPublish() {
        return publish == 1;
    }

    public void setPublish(boolean publish) {
        this.publish = (publish ? 1 : 0);
    }

    public boolean isPromote() {
        return promote == 1;
    }

    public void setPromote(boolean promote) {
        this.promote = (promote ? 1 : 0);
    }

    public String getItsRight() {
        return itsRight;
    }

    public void setItsRight(String itsRight) {
        this.itsRight = itsRight;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getBlipImage() {
        return blipImage;
    }

    public String getBlipImageSmall() {
        return blipImageSmall;
    }

    public void setBlipImageSmall(String blipImageSmall) {
        this.blipImageSmall = blipImageSmall;
    }

    public void setBlipImage(String blipImage) {
        this.blipImage = blipImage;
    }

    public List<Blip> getBlipsPicks() {
        return blipsPicks;
    }

    public void setBlipsPicks(List<Blip> blipsPicks) {
        this.blipsPicks = blipsPicks;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public boolean isPrinted() {
        return printed == 1;
    }

    public void setPrinted(boolean printed) {
        this.printed = (printed ? 1 : 0);
    }

    public String getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(String usedDate) {
        this.usedDate = usedDate;
    }

    public boolean getAddedtobb() {
        return addedtobb == 1;
    }

    public void setAddedtobb(boolean addedtobb) {
        this.addedtobb = (addedtobb ? 1 : 0);
    }

    public int getMyVote() {
        return myVote;
    }

    public void setMyVote(int myVote) {
        this.myVote = myVote;
    }

    public int getVideotype() {
        return videotype;
    }

    public void setVideotype(int videotype) {
        this.videotype = videotype;
    }

    public int getPositiveVotesCount() {
        return positiveVotesCount;
    }

    public void setPositiveVotesCount(int positiveVotesCount) {
        this.positiveVotesCount = positiveVotesCount;
    }

    public int getNegativeVotesCount() {
        return negativeVotesCount;
    }

    public void setNegativeVotesCount(int negativeVotesCount) {
        this.negativeVotesCount = negativeVotesCount;
    }

    public int getExpDay() {
        return expDay;
    }

    public void setExpDay(int expDay) {
        this.expDay = expDay;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public String getBlipvideo() {
        return blipvideo;
    }

    public void setBlipvideo(String blipvideo) {
        this.blipvideo = blipvideo;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getCouponBookCount() {
        return couponBookCount;
    }

    public void setCouponBookCount(Integer couponBookCount) {
        this.couponBookCount = couponBookCount;
    }

    public Integer getMobileCount() {
        return mobileCount;
    }

    public void setMobileCount(Integer mobileCount) {
        this.mobileCount = mobileCount;
    }

    public Integer getPrintedCount() {
        return printedCount;
    }

    public void setPrintedCount(Integer printedCount) {
        this.printedCount = printedCount;
    }

    public Integer getQrCount() {
        return qrCount;
    }

    public void setQrCount(Integer qrCount) {
        this.qrCount = qrCount;
    }

    public int getSkyClosest() {
        return skyClosest;
    }

    public void setSkyClosest(int skyClosest) {
        this.skyClosest = skyClosest;
    }

    public List<SocialMedia> getSocial() {
        return social;
    }

    public void setSocial(List<SocialMedia> social) {
        this.social = social;
    }

    public Blip copyBlip() {
        Blip blip = new Blip();
        blip.setCouponId(couponId);
        blip.setCouponTitle(couponTitle);

        return blip;
    }
}