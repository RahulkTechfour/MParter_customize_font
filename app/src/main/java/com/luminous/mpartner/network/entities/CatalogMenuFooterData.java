package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CatalogMenuFooterData {

    @SerializedName("Message")
    @Expose
    String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("product_category")
    @Expose
    private List<ProductCategoryEntity> productCategory = null;

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

    public List<ProductCategoryEntity> getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(List<ProductCategoryEntity> productCategory) {
        this.productCategory = productCategory;
    }
}
