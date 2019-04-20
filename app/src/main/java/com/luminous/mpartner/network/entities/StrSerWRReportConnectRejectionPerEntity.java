package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StrSerWRReportConnectRejectionPerEntity {

    @SerializedName("Total")
    @Expose
    private String total;
    @SerializedName("My")
    @Expose
    private String my;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMy() {
        return my;
    }

    public void setMy(String my) {
        this.my = my;
    }

}
