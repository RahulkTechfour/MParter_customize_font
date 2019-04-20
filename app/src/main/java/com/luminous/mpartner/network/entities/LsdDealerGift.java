
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LsdDealerGift {

    @SerializedName("GiftId")
    @Expose
    private Integer giftId;
    @SerializedName("GiftName")
    @Expose
    private String giftName;
    @SerializedName("GiftDescription")
    @Expose
    private String giftDescription;
    @SerializedName("GiftImage")
    @Expose
    private String giftImage;

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftDescription() {
        return giftDescription;
    }

    public void setGiftDescription(String giftDescription) {
        this.giftDescription = giftDescription;
    }

    public String getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(String giftImage) {
        this.giftImage = giftImage;
    }

}
