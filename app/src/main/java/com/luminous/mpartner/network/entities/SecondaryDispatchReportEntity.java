
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecondaryDispatchReportEntity {

    @SerializedName("DlrCode")
    @Expose
    private String dlrCode;
    @SerializedName("DlrName")
    @Expose
    private String dlrName;
    @SerializedName("SKUModelName")
    @Expose
    private String sKUModelName;
    @SerializedName("Quantity")
    @Expose
    private String quantity;
    @SerializedName("DispatchDate")
    @Expose
    private String dispatchDate;
    @SerializedName("WithSerialNumber")
    @Expose
    private String withSerialNumber;
    @SerializedName("WithoutSerialNumber")
    @Expose
    private String withoutSerialNumber;

    public String getDlrCode() {
        return dlrCode;
    }

    public void setDlrCode(String dlrCode) {
        this.dlrCode = dlrCode;
    }

    public String getDlrName() {
        return dlrName;
    }

    public void setDlrName(String dlrName) {
        this.dlrName = dlrName;
    }

    public String getSKUModelName() {
        return sKUModelName;
    }

    public void setSKUModelName(String sKUModelName) {
        this.sKUModelName = sKUModelName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(String dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getWithSerialNumber() {
        return withSerialNumber;
    }

    public void setWithSerialNumber(String withSerialNumber) {
        this.withSerialNumber = withSerialNumber;
    }

    public String getWithoutSerialNumber() {
        return withoutSerialNumber;
    }

    public void setWithoutSerialNumber(String withoutSerialNumber) {
        this.withoutSerialNumber = withoutSerialNumber;
    }

}
