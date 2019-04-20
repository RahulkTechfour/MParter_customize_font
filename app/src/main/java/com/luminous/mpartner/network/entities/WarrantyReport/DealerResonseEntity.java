
package com.luminous.mpartner.network.entities.WarrantyReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerResonseEntity {

    @SerializedName("dlr_code")
    @Expose
    private String dlrCode;
    @SerializedName("dlr_name")
    @Expose
    private String dlrName;
    @SerializedName("sub_Home_Ups")
    @Expose
    private String subHomeUps;
    @SerializedName("sub_Bty")
    @Expose
    private String subBty;
    @SerializedName("sub_HKVA")
    @Expose
    private String subHKVA;
    @SerializedName("sub_Home_Panel")
    @Expose
    private String subHomePanel;
    @SerializedName("sub_Home_Stabl")
    @Expose
    private String subHomeStabl;
    @SerializedName("sub_Total")
    @Expose
    private String subTotal;
    @SerializedName("accpt_Home_Ups")
    @Expose
    private String accptHomeUps;
    @SerializedName("accpt_Bty")
    @Expose
    private String accptBty;
    @SerializedName("accpt_HKVA")
    @Expose
    private String accptHKVA;
    @SerializedName("accpt_Home_Panel")
    @Expose
    private String accptHomePanel;
    @SerializedName("accpt_Home_Stabl")
    @Expose
    private String accptHomeStabl;
    @SerializedName("accpt_Total")
    @Expose
    private String accptTotal;

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

    public String getSubHomeUps() {
        return subHomeUps;
    }

    public void setSubHomeUps(String subHomeUps) {
        this.subHomeUps = subHomeUps;
    }

    public String getSubBty() {
        return subBty;
    }

    public void setSubBty(String subBty) {
        this.subBty = subBty;
    }

    public String getSubHKVA() {
        return subHKVA;
    }

    public void setSubHKVA(String subHKVA) {
        this.subHKVA = subHKVA;
    }

    public String getSubHomePanel() {
        return subHomePanel;
    }

    public void setSubHomePanel(String subHomePanel) {
        this.subHomePanel = subHomePanel;
    }

    public String getSubHomeStabl() {
        return subHomeStabl;
    }

    public void setSubHomeStabl(String subHomeStabl) {
        this.subHomeStabl = subHomeStabl;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getAccptHomeUps() {
        return accptHomeUps;
    }

    public void setAccptHomeUps(String accptHomeUps) {
        this.accptHomeUps = accptHomeUps;
    }

    public String getAccptBty() {
        return accptBty;
    }

    public void setAccptBty(String accptBty) {
        this.accptBty = accptBty;
    }

    public String getAccptHKVA() {
        return accptHKVA;
    }

    public void setAccptHKVA(String accptHKVA) {
        this.accptHKVA = accptHKVA;
    }

    public String getAccptHomePanel() {
        return accptHomePanel;
    }

    public void setAccptHomePanel(String accptHomePanel) {
        this.accptHomePanel = accptHomePanel;
    }

    public String getAccptHomeStabl() {
        return accptHomeStabl;
    }

    public void setAccptHomeStabl(String accptHomeStabl) {
        this.accptHomeStabl = accptHomeStabl;
    }

    public String getAccptTotal() {
        return accptTotal;
    }

    public void setAccptTotal(String accptTotal) {
        this.accptTotal = accptTotal;
    }

}
