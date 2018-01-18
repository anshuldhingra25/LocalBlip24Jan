package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytrobohachevskyy on 10/2/15.
 *
 * Class that represent simple response from server
 */
public class SimpleResponse {

    @SerializedName("response")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
