
package com.luminous.mpartner.network.entities.survey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyNotification {

    @SerializedName("SurveyId")
    @Expose
    private Integer surveyId;
    @SerializedName("Survey")
    @Expose
    private String survey;

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

}
