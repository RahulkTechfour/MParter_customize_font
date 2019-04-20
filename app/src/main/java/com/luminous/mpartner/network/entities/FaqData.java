package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqData {

    @SerializedName("Message")
    @Expose
    private String Message;
    @SerializedName("Status")
    @Expose
    private String Status;
    @SerializedName("faq_data")
    @Expose
    private List<FaqEntity> faqData = null;

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

    public List<FaqEntity> getFaqData() {
        return faqData;
    }

    public void setFaqData(List<FaqEntity> faqData) {
        this.faqData = faqData;
    }
}