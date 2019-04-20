package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrimaryReportEntity {

    @SerializedName("INV_NO")
    @Expose
    private String iNVNO;
    @SerializedName("INVOICE_DA")
    @Expose
    private String iNVOICEDA;
    @SerializedName("ITEM_CODE")
    @Expose
    private String iTEMCODE;
    @SerializedName("MATERIAL_DESC")
    @Expose
    private String mATERIALDESC;
    @SerializedName("INV_QTY")
    @Expose
    private String iNVQTY;
    @SerializedName("AMOUNT")
    @Expose
    private String aMOUNT;
    @SerializedName("DIVISION")
    @Expose
    private String dIVISION;

    public String getINVNO() {
        return iNVNO;
    }

    public void setINVNO(String iNVNO) {
        this.iNVNO = iNVNO;
    }

    public String getINVOICEDA() {
        return iNVOICEDA;
    }

    public void setINVOICEDA(String iNVOICEDA) {
        this.iNVOICEDA = iNVOICEDA;
    }

    public String getITEMCODE() {
        return iTEMCODE;
    }

    public void setITEMCODE(String iTEMCODE) {
        this.iTEMCODE = iTEMCODE;
    }

    public String getMATERIALDESC() {
        return mATERIALDESC;
    }

    public void setMATERIALDESC(String mATERIALDESC) {
        this.mATERIALDESC = mATERIALDESC;
    }

    public String getINVQTY() {
        return iNVQTY;
    }

    public void setINVQTY(String iNVQTY) {
        this.iNVQTY = iNVQTY;
    }

    public String getAMOUNT() {
        return aMOUNT;
    }

    public void setAMOUNT(String aMOUNT) {
        this.aMOUNT = aMOUNT;
    }

    public String getDIVISION() {
        return dIVISION;
    }

    public void setDIVISION(String dIVISION) {
        this.dIVISION = dIVISION;
    }

}