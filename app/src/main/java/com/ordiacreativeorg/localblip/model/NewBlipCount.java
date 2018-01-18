package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewBlipCount implements Serializable {

    @SerializedName("new_blips_count")
    @Expose
    private int newBlipsCount;

    public int getNewBlipsCount() { return newBlipsCount; }
    public void setNewBlipsCount( int newBlipsCount ) { this.newBlipsCount = newBlipsCount; }
}