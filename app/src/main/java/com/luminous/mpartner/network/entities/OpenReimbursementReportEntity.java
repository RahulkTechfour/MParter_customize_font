
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenReimbursementReportEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("lsd_open_reimbursment_data")
    @Expose
    private List<LsdOpenReimbursmentDatum> lsdOpenReimbursmentData = null;

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

    public List<LsdOpenReimbursmentDatum> getLsdOpenReimbursmentData() {
        return lsdOpenReimbursmentData;
    }

    public void setLsdOpenReimbursmentData(List<LsdOpenReimbursmentDatum> lsdOpenReimbursmentData) {
        this.lsdOpenReimbursmentData = lsdOpenReimbursmentData;
    }

}
