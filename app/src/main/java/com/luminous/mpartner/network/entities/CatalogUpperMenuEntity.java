
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatalogUpperMenuEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("product_catalog_upper")
    @Expose
    private List<ProductCatalogUpper> productCatalogUpper = null;

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

    public List<ProductCatalogUpper> getProductCatalogUpper() {
        return productCatalogUpper;
    }

    public void setProductCatalogUpper(List<ProductCatalogUpper> productCatalogUpper) {
        this.productCatalogUpper = productCatalogUpper;
    }

}
