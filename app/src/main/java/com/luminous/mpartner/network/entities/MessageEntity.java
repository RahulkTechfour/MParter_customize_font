
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageEntity {

    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

}
