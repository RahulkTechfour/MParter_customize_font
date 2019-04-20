package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SerWRGetCityNameEntity {

    @SerializedName("DistId")
    @Expose
    private String distId;
    @SerializedName("DistName")
    @Expose
    private String distName;

    public String getDistId() {
        return distId;
    }

    public void setDistId(String distId) {
        this.distId = distId;
    }

    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }

}