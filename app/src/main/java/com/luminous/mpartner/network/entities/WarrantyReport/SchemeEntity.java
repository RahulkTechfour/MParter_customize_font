
package com.luminous.mpartner.network.entities.WarrantyReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SchemeEntity {

    @SerializedName("scheme_name")
    @Expose
    private String schemeName;
    @SerializedName("scheme_value")
    @Expose
    private String schemeValue;

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeValue() {
        return schemeValue;
    }

    public void setSchemeValue(String schemeValue) {
        this.schemeValue = schemeValue;
    }

}
