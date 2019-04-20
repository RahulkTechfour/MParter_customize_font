package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContactUsDetail {
    @SerializedName("Message")
    @Expose
    private String Message;
    @SerializedName("Status")
    @Expose
    private String Status;
    @SerializedName("contactus")
    @Expose
    private List<ContactUsEntity> contactus = null;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<ContactUsEntity> getContactus() {
        return contactus;
    }

    public void setContactus(List<ContactUsEntity> contactus) {
        this.contactus = contactus;
    }
}

