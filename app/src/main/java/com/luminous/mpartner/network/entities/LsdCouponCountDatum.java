
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LsdCouponCountDatum {

    @SerializedName("EligibleCouponCount")
    @Expose
    private int eligibleCouponCount;
    @SerializedName("ActivatedCouponCount")
    @Expose
    private int activatedCouponCount;
    @SerializedName("BalanceCouponCount")
    @Expose
    private int balanceCouponCount;
    @SerializedName("CouponReimbursment")
    @Expose
    private int couponReimbursment;
    @SerializedName("OpenReimbursment")
    @Expose
    private int openReimbursment;
    @SerializedName("Gold_EligibleCouponCount")
    @Expose
    private int Gold_EligibleCouponCount;
    @SerializedName("Gold_ActivatedCouponCount")
    @Expose
    private int Gold_ActivatedCouponCount;
    @SerializedName("Gold_BalanceCouponCount")
    @Expose
    private int Gold_BalanceCouponCount;

    public int getEligibleCouponCount() {
        return eligibleCouponCount;
    }

    public void setEligibleCouponCount(int eligibleCouponCount) {
        this.eligibleCouponCount = eligibleCouponCount;
    }

    public int getActivatedCouponCount() {
        return activatedCouponCount;
    }

    public void setActivatedCouponCount(int activatedCouponCount) {
        this.activatedCouponCount = activatedCouponCount;
    }

    public int getBalanceCouponCount() {
        return balanceCouponCount;
    }

    public void setBalanceCouponCount(int balanceCouponCount) {
        this.balanceCouponCount = balanceCouponCount;
    }

    public int getCouponReimbursment() {
        return couponReimbursment;
    }

    public void setCouponReimbursment(int couponReimbursment) {
        this.couponReimbursment = couponReimbursment;
    }

    public int getOpenReimbursment() {
        return openReimbursment;
    }

    public void setOpenReimbursment(int openReimbursment) {
        this.openReimbursment = openReimbursment;
    }

    public int getGold_EligibleCouponCount() {
        return Gold_EligibleCouponCount;
    }

    public void setGold_EligibleCouponCount(int gold_EligibleCouponCount) {
        Gold_EligibleCouponCount = gold_EligibleCouponCount;
    }

    public int getGold_ActivatedCouponCount() {
        return Gold_ActivatedCouponCount;
    }

    public void setGold_ActivatedCouponCount(int gold_ActivatedCouponCount) {
        Gold_ActivatedCouponCount = gold_ActivatedCouponCount;
    }

    public int getGold_BalanceCouponCount() {
        return Gold_BalanceCouponCount;
    }

    public void setGold_BalanceCouponCount(int gold_BalanceCouponCount) {
        Gold_BalanceCouponCount = gold_BalanceCouponCount;
    }
}
