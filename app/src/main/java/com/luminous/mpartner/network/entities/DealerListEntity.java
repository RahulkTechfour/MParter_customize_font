
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealerListEntity {

    //http://166.62.100.102/Api/MpartnerApi/Customer_permission_data?user_id=9999998&app_version=3.9&appversion_code=28&device_id=f8ef23b02cd86042&device_name=SM-M305F&os_type=Android&os_version_code=27&os_version_name=8.1.0&ip_address=0.0.0.0&language=EN&screen_name=HomePageActivity.class&network_type=3g&network_operator=IDEA%20|%20Idea&time_captured=1552505554231&channel=M
    //http://166.62.100.102/Api/MpartnerApi/Customer_permission_data?user_id=9999998&token=a60957aab979dfb2a6b0218c5c6b386a&app_version=3.9&appversion_code=28&device_id=d039734abac691a1&device_name=ONEPLUS%20A6000&os_type=Android&os_version_code=28&os_version_name=9&ip_address=192.168.0.20&language=EN&screen_name=HomePageActivity.class&network_type=wifi&network_operator=airtel&time_captured=1552505909002&channel=M

    @SerializedName("DEALER_COD")
    @Expose
    private String dEALERCOD;
    @SerializedName("DEALER_NAME")
    @Expose
    private String dEALERNAME;
    @SerializedName("CITY_DESC")
    @Expose
    private String cITYDESC;
    @SerializedName("STATE_DESC")
    @Expose
    private String sTATEDESC;
    @SerializedName("MOBILE1")
    @Expose
    private String mOBILE1;
    @SerializedName("STATUS_DES")
    @Expose
    private String sTATUSDES;

    public String getDEALERCOD() {
        return dEALERCOD;
    }

    public void setDEALERCOD(String dEALERCOD) {
        this.dEALERCOD = dEALERCOD;
    }

    public String getDEALERNAME() {
        return dEALERNAME;
    }

    public void setDEALERNAME(String dEALERNAME) {
        this.dEALERNAME = dEALERNAME;
    }

    public String getCITYDESC() {
        return cITYDESC;
    }

    public void setCITYDESC(String cITYDESC) {
        this.cITYDESC = cITYDESC;
    }

    public String getSTATEDESC() {
        return sTATEDESC;
    }

    public void setSTATEDESC(String sTATEDESC) {
        this.sTATEDESC = sTATEDESC;
    }

    public String getMOBILE1() {
        return mOBILE1;
    }

    public void setMOBILE1(String mOBILE1) {
        this.mOBILE1 = mOBILE1;
    }

    public String getSTATUSDES() {
        return sTATUSDES;
    }

    public void setSTATUSDES(String sTATUSDES) {
        this.sTATUSDES = sTATUSDES;
    }

}
