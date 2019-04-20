
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SchemePointSlabEntity {

    @SerializedName("Points_earned")
    @Expose
    private String pointsEarned;
    @SerializedName("Current_Slab")
    @Expose
    private String currentSlab;
    @SerializedName("Point_Next_Slab")
    @Expose
    private String pointNextSlab;

    public String getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(String pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getCurrentSlab() {
        return currentSlab;
    }

    public void setCurrentSlab(String currentSlab) {
        this.currentSlab = currentSlab;
    }

    public String getPointNextSlab() {
        return pointNextSlab;
    }

    public void setPointNextSlab(String pointNextSlab) {
        this.pointNextSlab = pointNextSlab;
    }

}
