
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductSearch {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("productioncatalog_name")
    @Expose
    private String productioncatalogName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductioncatalogName() {
        return productioncatalogName;
    }

    public void setProductioncatalogName(String productioncatalogName) {
        this.productioncatalogName = productioncatalogName;
    }

}
