
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResponseEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("product_search")
    @Expose
    private List<ProductSearch> productSearch = null;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ProductSearch> getProductSearch() {
        return productSearch;
    }

    public void setProductSearch(List<ProductSearch> productSearch) {
        this.productSearch = productSearch;
    }

}