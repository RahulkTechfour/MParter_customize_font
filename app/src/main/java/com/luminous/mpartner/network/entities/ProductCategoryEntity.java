package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductCategoryEntity {

    @SerializedName("product_category_name")
    @Expose
    private String productCatagoryName;

    @SerializedName("url_product_category_pdf")
    @Expose
    private String urlProductCategoryPdf;
    @SerializedName("Id")
    @Expose
    private int id;

    public String getProductCatagoryName() {
        return productCatagoryName;
    }

    public String getUrlProductCategoryPdf() {
        return urlProductCategoryPdf;
    }

    public int getId() {
        return id;
    }
    public void setProductCatagoryName(String productCatagoryName) {
        this.productCatagoryName = productCatagoryName;
    }

    public void setUrlProductCategoryPdf(String urlProductCategoryPdf) {
        this.urlProductCategoryPdf = urlProductCategoryPdf;
    }

    public void setId(int id) {
        this.id = id;
    }
}
