
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecondarySalesReportEntity {

    @SerializedName("SKUModelId")
    @Expose
    private String sKUModelId;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("SKUModelName")
    @Expose
    private String sKUModelName;
    @SerializedName("TAvlQuantity")
    @Expose
    private String tAvlQuantity;

    public String getSKUModelId() {
        return sKUModelId;
    }

    public void setSKUModelId(String sKUModelId) {
        this.sKUModelId = sKUModelId;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getSKUModelName() {
        return sKUModelName;
    }

    public void setSKUModelName(String sKUModelName) {
        this.sKUModelName = sKUModelName;
    }

    public String getTAvlQuantity() {
        return tAvlQuantity;
    }

    public void setTAvlQuantity(String tAvlQuantity) {
        this.tAvlQuantity = tAvlQuantity;
    }

}
