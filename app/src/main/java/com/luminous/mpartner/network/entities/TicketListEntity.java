package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.luminous.mpartner.network.GetticketDatum;

import java.util.List;

public class TicketListEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("getticket_data")
    @Expose
    private List<GetticketDatum> getticketData = null;

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

    public List<GetticketDatum> getGetticketData() {
        return getticketData;
    }

    public void setGetticketData(List<GetticketDatum> getticketData) {
        this.getticketData = getticketData;
    }

}
