package com.luminous.mpartner.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetticketDatum {
    @SerializedName("serialno")
    @Expose
    private String serialno;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("createdby")
    @Expose
    private String createdby;
    @SerializedName("connectplus_msg")
    @Expose
    private Object connectplusMsg;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("Id")
    @Expose
    private int id;

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public Object getConnectplusMsg() {
        return connectplusMsg;
    }

    public void setConnectplusMsg(Object connectplusMsg) {
        this.connectplusMsg = connectplusMsg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
