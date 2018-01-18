package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;

/**
 * Created by dmytrobohachevskyy on 10/5/15.
 * <p>
 * This class represent notification model
 */
class Notification {
    /*
    "1": {
        "category": "Automotive & Industrial ",
        "catid": "1"
      }
     */

    @Expose
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Expose
    private String catid;

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }
}
