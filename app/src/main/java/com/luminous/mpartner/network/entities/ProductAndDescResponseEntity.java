
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductAndDescResponseEntity {

    @SerializedName("MaterialCode")
    @Expose
    private String materialCode;
    @SerializedName("MaterialDescription")
    @Expose
    private String materialDescription;
    @SerializedName("Channel")
    @Expose
    private String channel;
    @SerializedName("Division")
    @Expose
    private String division;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

}
