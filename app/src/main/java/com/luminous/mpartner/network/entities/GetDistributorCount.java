
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDistributorCount {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("lsd_coupon_count_data")
    @Expose
    private List<LsdCouponCountDatum> lsdCouponCountData = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LsdCouponCountDatum> getLsdCouponCountData() {
        return lsdCouponCountData;
    }

    public void setLsdCouponCountData(List<LsdCouponCountDatum> lsdCouponCountData) {
        this.lsdCouponCountData = lsdCouponCountData;
    }

}
