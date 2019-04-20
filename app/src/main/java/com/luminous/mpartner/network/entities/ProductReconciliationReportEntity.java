
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductReconciliationReportEntity {

    @SerializedName("Product_Type")
    @Expose
    private String productType;
    @SerializedName("Model_Name")
    @Expose
    private String modelName;
    @SerializedName("Primary_Customer")
    @Expose
    private String primaryCustomer;
    @SerializedName("Primary_Billed_Date")
    @Expose
    private String primaryBilledDate;
    @SerializedName("Dealer_Name")
    @Expose
    private String dealerName;
    @SerializedName("Sec_Billed_date")
    @Expose
    private String secBilledDate;
    @SerializedName("Customer_Name")
    @Expose
    private String customerName;
    @SerializedName("Customer_No")
    @Expose
    private String customerNo;
    @SerializedName("Tertiary_Date")
    @Expose
    private String tertiaryDate;
    @SerializedName("City")
    @Expose
    private String city;

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPrimaryCustomer() {
        return primaryCustomer;
    }

    public void setPrimaryCustomer(String primaryCustomer) {
        this.primaryCustomer = primaryCustomer;
    }

    public String getPrimaryBilledDate() {
        return primaryBilledDate;
    }

    public void setPrimaryBilledDate(String primaryBilledDate) {
        this.primaryBilledDate = primaryBilledDate;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getSecBilledDate() {
        return secBilledDate;
    }

    public void setSecBilledDate(String secBilledDate) {
        this.secBilledDate = secBilledDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getTertiaryDate() {
        return tertiaryDate;
    }

    public void setTertiaryDate(String tertiaryDate) {
        this.tertiaryDate = tertiaryDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
