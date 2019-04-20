
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveDealerSacnCodeEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("lsd_dealer_gift")
    @Expose
    private List<LsdDealerGift> lsdDealerGift = null;

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

    public List<LsdDealerGift> getLsdDealerGift() {
        return lsdDealerGift;
    }

    public void setLsdDealerGift(List<LsdDealerGift> lsdDealerGift) {
        this.lsdDealerGift = lsdDealerGift;
    }

}
