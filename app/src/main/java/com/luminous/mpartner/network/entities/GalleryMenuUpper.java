
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryMenuUpper {

    @SerializedName("gallery_menu_upper_id")
    @Expose
    private Integer galleryMenuUpperId;
    @SerializedName("gallery_menu_upper_name")
    @Expose
    private String galleryMenuUpperName;

    public Integer getGalleryMenuUpperId() {
        return galleryMenuUpperId;
    }

    public void setGalleryMenuUpperId(Integer galleryMenuUpperId) {
        this.galleryMenuUpperId = galleryMenuUpperId;
    }

    public String getGalleryMenuUpperName() {
        return galleryMenuUpperName;
    }

    public void setGalleryMenuUpperName(String galleryMenuUpperName) {
        this.galleryMenuUpperName = galleryMenuUpperName;
    }

}
