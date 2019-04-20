
package com.luminous.mpartner.network.entities.WarrantyReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerListEntity {

    @SerializedName("dlr_code")
    @Expose
    private String dlrCode;
    @SerializedName("dlr_name")
    @Expose
    private String dlrName;

    public String getDlrCode() {
        return dlrCode;
    }

    public void setDlrCode(String dlrCode) {
        this.dlrCode = dlrCode;
    }

    public String getDlrName() {
        return dlrName;
    }

    public void setDlrName(String dlrName) {
        this.dlrName = dlrName;
    }

}
