
package com.luminous.mpartner.network.entities.Notification;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationResponseEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("survey_notification_alert")
    @Expose
    private List<SurveyNotificationAlert> surveyNotificationAlert = null;

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

    public List<SurveyNotificationAlert> getSurveyNotificationAlert() {
        return surveyNotificationAlert;
    }

    public void setSurveyNotificationAlert(List<SurveyNotificationAlert> surveyNotificationAlert) {
        this.surveyNotificationAlert = surveyNotificationAlert;
    }

}
