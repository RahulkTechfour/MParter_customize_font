package com.luminous.mpartner.dynamic_home.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PriceData {
    @SerializedName("Success")
    @Expose
    private boolean success;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Price_data")
    @Expose
    private List<HomeCardEntity> dynamicHomePage = null;
    @SerializedName("Message")
    @Expose
    String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HomeCardEntity> getDynamicHomePage() {
        return dynamicHomePage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setDynamicHomePage(List<HomeCardEntity> dynamicHomePage) {
        this.dynamicHomePage = dynamicHomePage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}