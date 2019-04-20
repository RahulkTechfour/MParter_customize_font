
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewSalesOrderResponseEntity {

    @SerializedName("SALES_ORDE")
    @Expose
    private String sALESORDE;
    @SerializedName("ORDER_DATE")
    @Expose
    private String oRDERDATE;
    @SerializedName("MATERIAL_NUMBER")
    @Expose
    private String mATERIALNUMBER;
    @SerializedName("QUANTITY")
    @Expose
    private String qUANTITY;
    @SerializedName("NET_PRICE")
    @Expose
    private String nETPRICE;
    @SerializedName("NET_VALUE")
    @Expose
    private String nETVALUE;
    @SerializedName("STATUS")
    @Expose
    private String sTATUS;

    public String getSALESORDE() {
        return sALESORDE;
    }

    public void setSALESORDE(String sALESORDE) {
        this.sALESORDE = sALESORDE;
    }

    public String getORDERDATE() {
        return oRDERDATE;
    }

    public void setORDERDATE(String oRDERDATE) {
        this.oRDERDATE = oRDERDATE;
    }

    public String getMATERIALNUMBER() {
        return mATERIALNUMBER;
    }

    public void setMATERIALNUMBER(String mATERIALNUMBER) {
        this.mATERIALNUMBER = mATERIALNUMBER;
    }

    public String getQUANTITY() {
        return qUANTITY;
    }

    public void setQUANTITY(String qUANTITY) {
        this.qUANTITY = qUANTITY;
    }

    public String getNETPRICE() {
        return nETPRICE;
    }

    public void setNETPRICE(String nETPRICE) {
        this.nETPRICE = nETPRICE;
    }

    public String getNETVALUE() {
        return nETVALUE;
    }

    public void setNETVALUE(String nETVALUE) {
        this.nETVALUE = nETVALUE;
    }

    public String getSTATUS() {
        return sTATUS;
    }

    public void setSTATUS(String sTATUS) {
        this.sTATUS = sTATUS;
    }

}
