
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerLedgerReportEntity {

    @SerializedName("DOC_DATE")
    @Expose
    private String dOCDATE;
    @SerializedName("DOC_NO")
    @Expose
    private String dOCNO;
    @SerializedName("REF_ORG_UN")
    @Expose
    private String rEFORGUN;
    @SerializedName("DEBIT_AMOUNT")
    @Expose
    private String dEBITAMOUNT;
    @SerializedName("CREDIT_AMOUNT")
    @Expose
    private String cREDITAMOUNT;
    @SerializedName("CHANNEL_TXT")
    @Expose
    private String cHANNELTXT;
    @SerializedName("DIVISION_TXT")
    @Expose
    private String dIVISIONTXT;
    @SerializedName("TOTAL_CREDIT")
    @Expose
    private String tOTALCREDIT;
    @SerializedName("CDRC")
    @Expose
    private String cDRC;

    public String getDOCDATE() {
        return dOCDATE;
    }

    public void setDOCDATE(String dOCDATE) {
        this.dOCDATE = dOCDATE;
    }

    public String getDOCNO() {
        return dOCNO;
    }

    public void setDOCNO(String dOCNO) {
        this.dOCNO = dOCNO;
    }

    public String getREFORGUN() {
        return rEFORGUN;
    }

    public void setREFORGUN(String rEFORGUN) {
        this.rEFORGUN = rEFORGUN;
    }

    public String getDEBITAMOUNT() {
        return dEBITAMOUNT;
    }

    public void setDEBITAMOUNT(String dEBITAMOUNT) {
        this.dEBITAMOUNT = dEBITAMOUNT;
    }

    public String getCREDITAMOUNT() {
        return cREDITAMOUNT;
    }

    public void setCREDITAMOUNT(String cREDITAMOUNT) {
        this.cREDITAMOUNT = cREDITAMOUNT;
    }

    public String getCHANNELTXT() {
        return cHANNELTXT;
    }

    public void setCHANNELTXT(String cHANNELTXT) {
        this.cHANNELTXT = cHANNELTXT;
    }

    public String getDIVISIONTXT() {
        return dIVISIONTXT;
    }

    public void setDIVISIONTXT(String dIVISIONTXT) {
        this.dIVISIONTXT = dIVISIONTXT;
    }

    public String getTOTALCREDIT() {
        return tOTALCREDIT;
    }

    public void setTOTALCREDIT(String tOTALCREDIT) {
        this.tOTALCREDIT = tOTALCREDIT;
    }

    public String getCDRC() {
        return cDRC;
    }

    public void setCDRC(String cDRC) {
        this.cDRC = cDRC;
    }

}
