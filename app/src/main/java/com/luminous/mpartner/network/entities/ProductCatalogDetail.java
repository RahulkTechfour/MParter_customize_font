
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductCatalogDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("keyfeature")
    @Expose
    private String keyfeature;
    @SerializedName("warrenty")
    @Expose
    private String warrenty;
    @SerializedName("tech_specification")
    @Expose
    private List<TechSpecification> techSpecification = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

}
