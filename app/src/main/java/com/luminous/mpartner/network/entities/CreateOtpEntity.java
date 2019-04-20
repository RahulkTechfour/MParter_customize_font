package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOtpEntity {

    @SerializedName("des")
    @Expose
    private String Message;
    @SerializedName("Code")
    @Expose
    private String Status;

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
}
