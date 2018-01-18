package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/10/2015
 */
public class Category implements Serializable {

    @SerializedName(value="categoryid", alternate={"id", "catid"})
    @Expose
    private int categoryId;

    @SerializedName("category")
    @Expose
    private String categoryName;

    @SerializedName("subcategory")
    @Expose
    private List<SubCategory> subCategories;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    private void addSubCategory (SubCategory subCategory){
        if (subCategories == null) subCategories = new ArrayList<>();
        subCategories.add(subCategory);
    }

    public Category copyCategory(){
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName(categoryName);
        for (SubCategory subCategory : subCategories){
            category.addSubCategory(subCategory.copySubCategory());
        }
        return category;
    }
}
