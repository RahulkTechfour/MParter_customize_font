package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketEntity {
    @SerializedName("user_id")
    @Expose
    String user_id;

    @SerializedName("attachmentname")
    @Expose
    String attachmentname;

    @SerializedName("attachment")
    @Expose
    String attachment;

    @SerializedName("serialno")
    @Expose
    String serialno;
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("connectplus_message")
    @Expose
    String connectplus_message;
    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("app_version")
    @Expose
    private String app_version;
    @SerializedName("appversion_code")
    @Expose
    private String appversion_code;
    @SerializedName("device_id")
    @Expose
    private String device_id;
    @SerializedName("device_name")
    @Expose
    private String device_name;
    @SerializedName("Os_type")
    @Expose
    private String Os_type;
    @SerializedName("Os_version_name")
    @Expose
    private String Os_version_name;
    @SerializedName("Os_version_code")
    @Expose
    private String Os_version_code;
    @SerializedName("ip_address")
    @Expose
    private String ip_address;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("screen_name")
    @Expose
    private String screen_name;
    @SerializedName("network_type")
    @Expose
    private String network_type;
    @SerializedName("Network_operator")
    @Expose
    private String Network_operator;
    @SerializedName("time_captured")
    @Expose
    private String time_captured;
    @SerializedName("channel")
    @Expose
    private String channel;

    @SerializedName("ticketid")
    @Expose
    private String ticketid;

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAttachmentname() {
        return attachmentname;
    }

    public void setAttachmentname(String attachmentname) {
        this.attachmentname = attachmentname;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConnectplus_message() {
        return connectplus_message;
    }

    public void setConnectplus_message(String connectplus_message) {
        this.connectplus_message = connectplus_message;
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

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getAppversion_code() {
        return appversion_code;
    }

    public void setAppversion_code(String appversion_code) {
        this.appversion_code = appversion_code;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getOs_type() {
        return Os_type;
    }

    public void setOs_type(String os_type) {
        Os_type = os_type;
    }

    public String getOs_version_name() {
        return Os_version_name;
    }

    public void setOs_version_name(String os_version_name) {
        Os_version_name = os_version_name;
    }

    public String getOs_version_code() {
        return Os_version_code;
    }

    public void setOs_version_code(String os_version_code) {
        Os_version_code = os_version_code;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getNetwork_type() {
        return network_type;
    }

    public void setNetwork_type(String network_type) {
        this.network_type = network_type;
    }

    public String getNetwork_operator() {
        return Network_operator;
    }

    public void setNetwork_operator(String network_operator) {
        Network_operator = network_operator;
    }

    public String getTime_captured() {
        return time_captured;
    }

    public void setTime_captured(String time_captured) {
        this.time_captured = time_captured;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
