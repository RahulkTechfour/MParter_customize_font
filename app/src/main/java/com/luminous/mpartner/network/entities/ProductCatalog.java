
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductCatalog {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("productcatalog_image_url")
    @Expose
    private String productcatalogImageUrl;
    @SerializedName("productioncatalog_rating")
    @Expose
    private String productioncatalogRating;
    @SerializedName("productioncatalog_name")
    @Expose
    private String productioncatalogName;
    @SerializedName("keyfeature")
    @Expose
    private String keyfeature;
    @SerializedName("warrenty")
    @Expose
    private String warrenty;
    @SerializedName("attribute_name")
    @Expose
    private String attribute_name;
    @SerializedName("tech_specification")
    @Expose
    private List<TechSpecification> techSpecification = null;
    @SerializedName("productleveltwo")
    @Expose
    private String productleveltwo;


    public String getProductleveltwo() {
        return productleveltwo;
    }

    public void setProductleveltwo(String productleveltwo) {
        this.productleveltwo = productleveltwo;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductcatalogImageUrl() {
        return productcatalogImageUrl;
    }

    public void setProductcatalogImageUrl(String productcatalogImageUrl) {
        this.productcatalogImageUrl = productcatalogImageUrl;
    }

    public String getProductioncatalogRating() {
        return productioncatalogRating;
    }

    public void setProductioncatalogRating(String productioncatalogRating) {
        this.productioncatalogRating = productioncatalogRating;
    }

    public String getProductioncatalogName() {
        return productioncatalogName;
    }

    public void setProductioncatalogName(String productioncatalogName) {
        this.productioncatalogName = productioncatalogName;
    }

    public String getKeyfeature() {
        return keyfeature;
    }

    public void setKeyfeature(String keyfeature) {
        this.keyfeature = keyfeature;
    }

    public String getWarrenty() {
        return warrenty;
    }

    public void setWarrenty(String warrenty) {
        this.warrenty = warrenty;
    }

    public List<TechSpecification> getTechSpecification() {
        return techSpecification;
    }

    public void setTechSpecification(List<TechSpecification> techSpecification) {
        this.techSpecification = techSpecification;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }
}
