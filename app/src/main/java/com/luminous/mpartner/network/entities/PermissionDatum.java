
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermissionDatum {

    @SerializedName("ModuleName")
    @Expose
    private String moduleName;
    @SerializedName("ModuleText")
    @Expose
    private String moduleText;
    @SerializedName("CustomerType")
    @Expose
    private String customerType;
    @SerializedName("Permission")
    @Expose
    private String permission;
    @SerializedName("ModuleImage")
    @Expose
    private String moduleImage;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleText() {
        return moduleText;
    }

    public void setModuleText(String moduleText) {
        this.moduleText = moduleText;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getModuleImage() {
        return moduleImage;
    }

    public void setModuleImage(String moduleImage) {
        this.moduleImage = moduleImage;
    }
}
