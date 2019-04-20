
package com.luminous.mpartner.network.entities.WarrantyReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistWREntity {

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


    public String[] toStringArray(){

        String [] array = new String[4];

        array[0] = getTotal();
        array[1] = getAccepted();
        array[2] = getWRSPoint();
        array[3] = getRejected();

        return array;

    }


}
