
package com.luminous.mpartner.network.entities.Notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyNotificationAlert {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("Text")
    @Expose
    private String text;
    @SerializedName("isread")
    @Expose
    private boolean isread;
    @SerializedName("Imagename")
    @Expose
    private String imagename;
    @SerializedName("Imagepath")
    @Expose
    private String imagepath;
    @SerializedName("Date")
    @Expose
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
