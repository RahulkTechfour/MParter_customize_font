
package com.luminous.mpartner.network.entities.Media;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaListResponseEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("media_data")
    @Expose
    private List<MediaDatum> mediaData = null;

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

    public List<MediaDatum> getMediaData() {
        return mediaData;
    }

    public void setMediaData(List<MediaDatum> mediaData) {
        this.mediaData = mediaData;
    }

}
