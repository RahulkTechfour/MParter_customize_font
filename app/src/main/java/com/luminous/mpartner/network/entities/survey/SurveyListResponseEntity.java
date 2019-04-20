
package com.luminous.mpartner.network.entities.survey;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyListResponseEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("survey_notification")
    @Expose
    private List<SurveyNotification> surveyNotification = null;

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

    public List<SurveyNotification> getSurveyNotification() {
        return surveyNotification;
    }

    public void setSurveyNotification(List<SurveyNotification> surveyNotification) {
        this.surveyNotification = surveyNotification;
    }

}
