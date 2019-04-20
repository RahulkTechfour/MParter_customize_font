package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MSerWRDistListByDlrEntity {

    @SerializedName("dist_code")
    @Expose
    private String distCode;
    @SerializedName("dist_name")
    @Expose
    private String distName;

    public String getDistCode() {
        return distCode;
    }

    public void setDistCode(String distCode) {
        this.distCode = distCode;
    }

    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }

}