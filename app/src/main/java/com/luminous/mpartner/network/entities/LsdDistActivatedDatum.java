
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LsdDistActivatedDatum {

    @SerializedName("AlphanumericCode")
    @Expose
    private String alphanumericCode;
    @SerializedName("ActivatedDateTime")
    @Expose
    private String activatedDateTime;

    public String getAlphanumericCode() {
        return alphanumericCode;
    }

    public void setAlphanumericCode(String alphanumericCode) {
        this.alphanumericCode = alphanumericCode;
    }

    public String getActivatedDateTime() {
        return activatedDateTime;
    }

    public void setActivatedDateTime(String activatedDateTime) {
        this.activatedDateTime = activatedDateTime;
    }

}
