package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/19/2015
 */
public class ImageFile implements Serializable{


    @SerializedName("filename")
    @Expose
    private String fileName;
    @SerializedName("source")
    @Expose
    private String source;

    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
