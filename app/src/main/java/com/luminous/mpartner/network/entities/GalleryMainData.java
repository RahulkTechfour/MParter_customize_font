
package com.luminous.mpartner.network.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryMainData {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("gallery_data")
    @Expose
    private List<GalleryDatum> galleryData = null;

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

    public List<GalleryDatum> getGalleryData() {
        return galleryData;
    }

    public void setGalleryData(List<GalleryDatum> galleryData) {
        this.galleryData = galleryData;
    }

}
