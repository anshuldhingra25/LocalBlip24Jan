package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

/**
 * Created by dmytrobohachevskyy on 9/25/15.
 *
 * Model for member
 */
public class BlipAlert {


    @SerializedName("response")
    @Expose
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


}
