package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewTicketEntity {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("viewticket_data")
    @Expose
    private List<ViewticketDatum> viewticketData = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ViewticketDatum> getViewticketData() {
        return viewticketData;
    }

    public void setViewticketData(List<ViewticketDatum> viewticketData) {
        this.viewticketData = viewticketData;
    }

}
