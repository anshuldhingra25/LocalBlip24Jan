package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/19/2015
 */
public class CategoryWithImages implements Serializable {


    @SerializedName("categoryid")
    @Expose
    private String categoryId;
    @SerializedName("cattitle")
    @Expose
    private String categoryName;
    @SerializedName("files")
    @Expose
    private List<ImageFile> imageFiles;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<ImageFile> getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(List<ImageFile> imageFiles) {
        this.imageFiles = imageFiles;
    }

}
