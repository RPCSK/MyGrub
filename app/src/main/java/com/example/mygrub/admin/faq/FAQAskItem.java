package com.example.mygrub.admin.faq;

public class FAQAskItem {
    private String key, question, userID, userEmail;

    public FAQAskItem() {
    }

    public FAQAskItem(String key, String question, String userID, String userEmail) {
        this.key = key;
        this.question = question;
        this.userID = userID;
        this.userEmail = userEmail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
