package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactEntity {
    @SerializedName("name")
    @Expose
    private String Name;
    @SerializedName("email")
    @Expose
    private String Email;
    @SerializedName("message")
    @Expose
    private String Message;
    @SerializedName("contactusimage")
    @Expose
    private String Image;
    @SerializedName("filename")
    @Expose
    private String file;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @SerializedName("user_id")
    @Expose
    private String user_id;


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


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
