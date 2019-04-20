
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecondarySalesQuantitySummaryEntity {

    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("TAvlQuantity")
    @Expose
    private String tAvlQuantity;

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getTAvlQuantity() {
        return tAvlQuantity;
    }

    public void setTAvlQuantity(String tAvlQuantity) {
        this.tAvlQuantity = tAvlQuantity;
    }

}
