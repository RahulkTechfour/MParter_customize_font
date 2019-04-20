
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LsdDealerReport {

    @SerializedName("Barcode")
    @Expose
    private String barcode;
    @SerializedName("Gift")
    @Expose
    private String gift;
    @SerializedName("ActivatedDistName")
    @Expose
    private String activatedDistName;
    @SerializedName("SecretCode")
    @Expose
    private String secretCode;
    @SerializedName("ReimbursmentDate_Time")
    @Expose
    private String reimbursmentDateTime;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getActivatedDistName() {
        return activatedDistName;
    }

    public void setActivatedDistName(String activatedDistName) {
        this.activatedDistName = activatedDistName;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getReimbursmentDateTime() {
        return reimbursmentDateTime;
    }

    public void setReimbursmentDateTime(String reimbursmentDateTime) {
        this.reimbursmentDateTime = reimbursmentDateTime;
    }

}
