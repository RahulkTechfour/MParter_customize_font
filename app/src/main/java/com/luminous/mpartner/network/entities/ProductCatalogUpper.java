
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductCatalogUpper {

    @SerializedName("catalog_menu_upper_id")
    @Expose
    private Integer catalogMenuUpperId;
    @SerializedName("catalog_menu_upper_name")
    @Expose
    private String catalogMenuUpperName;
    @SerializedName("filter_key")
    @Expose
    private String filterKey;

    public Integer getCatalogMenuUpperId() {
        return catalogMenuUpperId;
    }

    public void setCatalogMenuUpperId(Integer catalogMenuUpperId) {
        this.catalogMenuUpperId = catalogMenuUpperId;
    }

    public String getCatalogMenuUpperName() {
        return catalogMenuUpperName;
    }

    public void setCatalogMenuUpperName(String catalogMenuUpperName) {
        this.catalogMenuUpperName = catalogMenuUpperName;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

}
