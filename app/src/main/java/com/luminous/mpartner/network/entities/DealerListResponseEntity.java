
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerListResponseEntity {

    @SerializedName("dlr_code")
    @Expose(serialize = true, deserialize = true)
    private String dlrCode;

    @SerializedName("dlr_name")
    @Expose(serialize = true, deserialize = true)
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
