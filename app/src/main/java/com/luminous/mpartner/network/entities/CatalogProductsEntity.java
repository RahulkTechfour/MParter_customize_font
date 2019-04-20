
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatalogProductsEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("product_catalog")
    @Expose
    private List<ProductCatalog> productCatalog = null;

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

    public List<ProductCatalog> getProductCatalog() {
        return productCatalog;
    }

    public void setProductCatalog(List<ProductCatalog> productCatalog) {
        this.productCatalog = productCatalog;
    }

}
