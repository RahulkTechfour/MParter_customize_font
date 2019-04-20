
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerPermissionEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("permission_data")
    @Expose
    private List<PermissionDatum> permissionData = null;

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

    public List<PermissionDatum> getPermissionData() {
        return permissionData;
    }

    public void setPermissionData(List<PermissionDatum> permissionData) {
        this.permissionData = permissionData;
    }

}
