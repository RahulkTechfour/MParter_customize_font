
package com.luminous.mpartner.network.entities.survey.SurveyQuestion;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyQuestionEntity {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("survey_notification_ques")
    @Expose
    private List<SurveyNotificationQue> surveyNotificationQues = null;

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

    public List<SurveyNotificationQue> getSurveyNotificationQues() {
        return surveyNotificationQues;
    }

    public void setSurveyNotificationQues(List<SurveyNotificationQue> surveyNotificationQues) {
        this.surveyNotificationQues = surveyNotificationQues;
    }

}
