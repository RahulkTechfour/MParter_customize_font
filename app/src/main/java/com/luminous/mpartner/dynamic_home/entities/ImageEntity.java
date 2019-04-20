package com.luminous.mpartner.dynamic_home.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageEntity {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("background_image")
    @Expose
    private String backgroundImage;
    @SerializedName("main_image")
    @Expose
    private String mainImage;
    @SerializedName("card_action")
    @Expose
    private String cardAction;
    @SerializedName("image_height")
    @Expose
    private String imageHeight;
    @SerializedName("image_width")
    @Expose
    private String imageWidth;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getCardAction() {
        return cardAction;
    }

    public void setCardAction(String cardAction) {
        this.cardAction = cardAction;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

}

