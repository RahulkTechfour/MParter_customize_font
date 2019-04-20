
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LsdOpenReimbursmentDatum {

    @SerializedName("DealerCode")
    @Expose
    private String dealerCode;
    @SerializedName("DealerName")
    @Expose
    private String dealerName;
    @SerializedName("AlphanumericCode")
    @Expose
    private String alphanumericCode;
    @SerializedName("GiftName")
    @Expose
    private String giftName;
    @SerializedName("DealerredemptionDateTime")
    @Expose
    private String dealerredemptionDateTime;
    @SerializedName("ActivationDateAndTime")
    @Expose
    private String activationDateAndTime;

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getAlphanumericCode() {
        return alphanumericCode;
    }

    public void setAlphanumericCode(String alphanumericCode) {
        this.alphanumericCode = alphanumericCode;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getDealerredemptionDateTime() {
        return dealerredemptionDateTime;
    }

    public void setDealerredemptionDateTime(String dealerredemptionDateTime) {
        this.dealerredemptionDateTime = dealerredemptionDateTime;
    }

    public String getActivationDateAndTime() {
        return activationDateAndTime;
    }

    public void setActivationDateAndTime(String activationDateAndTime) {
        this.activationDateAndTime = activationDateAndTime;
    }

}
