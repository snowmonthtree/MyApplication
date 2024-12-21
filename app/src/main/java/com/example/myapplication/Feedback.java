package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;

public class Feedback {

    @SerializedName("Feedback_ID")
    private String feedbackId;

    @SerializedName("User_ID")
    private String userId;  // Assuming that userId is represented as a String (like user name or userId)

    @SerializedName("Feedback_Content")
    private String feedbackContent;

    @SerializedName("Feedback_Date")
    private Timestamp feedbackDate;

    // Getter and Setter methods

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public Timestamp getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(Timestamp feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
}
