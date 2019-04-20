
package com.luminous.mpartner.network.entities.survey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;

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

}
