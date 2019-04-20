
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryUpperMenuData {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("gallery_menu_upper")
    @Expose
    private List<GalleryMenuUpper> galleryMenuUpper = null;

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

    public List<GalleryMenuUpper> getGalleryMenuUpper() {
        return galleryMenuUpper;
    }

    public void setGalleryMenuUpper(List<GalleryMenuUpper> galleryMenuUpper) {
        this.galleryMenuUpper = galleryMenuUpper;
    }

}
