
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerReportEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("lsd_dealer_report")
    @Expose
    private List<LsdDealerReport> lsdDealerReport = null;

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

    public List<LsdDealerReport> getLsdDealerReport() {
        return lsdDealerReport;
    }

    public void setLsdDealerReport(List<LsdDealerReport> lsdDealerReport) {
        this.lsdDealerReport = lsdDealerReport;
    }

}
