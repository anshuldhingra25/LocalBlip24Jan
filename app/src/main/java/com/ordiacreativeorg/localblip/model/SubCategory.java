package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class SubCategory implements Serializable{

    @SerializedName(value="subcategoryid", alternate={"subcatid"})
    @Expose
    private int subCategoryId;

    @SerializedName(value="title", alternate={"subcategory"})
    @Expose
    private String subCategoryName;

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public SubCategory copySubCategory(){
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryId(subCategoryId);
        subCategory.setSubCategoryName(subCategoryName);
        return subCategory;
    }
}
