
package com.luminous.mpartner.network.entities.EscalationMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EscalationMatrix {

    @SerializedName("ServiceCenterName")
    @Expose
    private String serviceCenterName;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Phoneno")
    @Expose
    private String phoneno;

    public String getServiceCenterName() {
        return serviceCenterName;
    }

    public void setServiceCenterName(String serviceCenterName) {
        this.serviceCenterName = serviceCenterName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

}
