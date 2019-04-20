
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LsdDealerRedeemedDatum {

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
    @SerializedName("ClaimSubmissionDate")
    @Expose
    private String claimSubmissionDate;
    @SerializedName("DistActivatedDateTime")
    @Expose
    private String distActivatedDateTime;
    @SerializedName("DealerredemptionDateTime")
    @Expose
    private String dealerredemptionDateTime;

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

    public String getClaimSubmissionDate() {
        return claimSubmissionDate;
    }

    public void setClaimSubmissionDate(String claimSubmissionDate) {
        this.claimSubmissionDate = claimSubmissionDate;
    }

    public String getDistActivatedDateTime() {
        return distActivatedDateTime;
    }

    public void setDistActivatedDateTime(String distActivatedDateTime) {
        this.distActivatedDateTime = distActivatedDateTime;
    }

    public String getDealerredemptionDateTime() {
        return dealerredemptionDateTime;
    }

    public void setDealerredemptionDateTime(String dealerredemptionDateTime) {
        this.dealerredemptionDateTime = dealerredemptionDateTime;
    }

}
