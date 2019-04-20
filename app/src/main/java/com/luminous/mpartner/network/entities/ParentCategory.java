
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParentCategory {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Parentcategoryname")
    @Expose
    private String parentcategoryname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParentcategoryname() {
        return parentcategoryname;
    }

    public void setParentcategoryname(String parentcategoryname) {
        this.parentcategoryname = parentcategoryname;
    }

}
