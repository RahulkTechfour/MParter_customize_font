
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WRStatusReportCardEntity {


    @SerializedName("Product")
    @Expose
    private String product;
    @SerializedName("Accepted")
    @Expose
    private String accepted;
    @SerializedName("Rejected")
    @Expose
    private String rejected;
    @SerializedName("Total")
    @Expose
    private String total;
    @SerializedName("WRS_Point")
    @Expose
    private String wRSPoint;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getRejected() {
        return rejected;
    }

    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getWRSPoint() {
        return wRSPoint;
    }

    public void setWRSPoint(String wRSPoint) {
        this.wRSPoint = wRSPoint;
    }

//    public void setTotal(String total) {
//        this.total = total;
//    }
//    public static String [] keys = {"HUPS", "Battery", "HKVA", "Panel", "Stabilizer", "Total"};
//
//    @Override
//    public String toString() {
//        return String.format("HUPS %s, Battery %s, HKVA %s, Panel %s, Stabilizer %s, Total %s",
//                getHUPS(), getBattery(), getHKVA(), getPanel(), getStabilizer(), getTotal());
//    }
}
