
package com.luminous.mpartner.network.entities.survey.SurveyQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyNotificationQue {

    @SerializedName("SurveyId")
    @Expose
    private Integer surveyId;
    @SerializedName("Survey")
    @Expose
    private String survey;
    @SerializedName("QuestionType")
    @Expose
    private String questionType;
    @SerializedName("QuestionTitle")
    @Expose
    private String questionTitle;
    @SerializedName("OptionA")
    @Expose
    private String optionA;
    @SerializedName("OptionB")
    @Expose
    private String optionB;
    @SerializedName("OptionC")
    @Expose
    private String optionC;
    @SerializedName("OptionD")
    @Expose
    private String optionD;
    @SerializedName("OptionE")
    @Expose
    private String optionE;
    @SerializedName("CorrectAns")
    @Expose
    private String correctAns;

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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getOptionE() {
        return optionE;
    }

    public void setOptionE(String optionE) {
        this.optionE = optionE;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

}
