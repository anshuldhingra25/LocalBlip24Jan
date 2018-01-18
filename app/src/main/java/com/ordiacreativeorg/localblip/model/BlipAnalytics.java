package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class BlipAnalytics implements Serializable {

    @SerializedName("by_location")
    @Expose
    private List<BlipStatDetailsByLocation> byLocations;

    @SerializedName("by_age")
    @Expose
    private List<BlipStatDetailsByAge> byAges;

    @SerializedName("by_gender")
    @Expose
    private List<BlipStatDetailsByGender> byGenders;

    public List<BlipStatDetailsByLocation> getByLocations() {
        return byLocations;
    }

    public void setByLocations(List<BlipStatDetailsByLocation> byLocations) {
        this.byLocations = byLocations;
    }

    public List<BlipStatDetailsByAge> getByAges() {
        return byAges;
    }

    public void setByAges(List<BlipStatDetailsByAge> byAges) {
        this.byAges = byAges;
    }

    public List<BlipStatDetailsByGender> getByGenders() {
        return byGenders;
    }

    public void setByGenders(List<BlipStatDetailsByGender> byGenders) {
        this.byGenders = byGenders;
    }
}
