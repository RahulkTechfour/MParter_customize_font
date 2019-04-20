
package com.luminous.mpartner.network.entities.Media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaDatum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("gallery_video_name")
    @Expose
    private String galleryVideoName;
    @SerializedName("gallery_video_url")
    @Expose
    private String galleryVideoUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGalleryVideoName() {
        return galleryVideoName;
    }

    public void setGalleryVideoName(String galleryVideoName) {
        this.galleryVideoName = galleryVideoName;
    }

    public String getGalleryVideoUrl() {
        return galleryVideoUrl;
    }

    public void setGalleryVideoUrl(String galleryVideoUrl) {
        this.galleryVideoUrl = galleryVideoUrl;
    }

}
