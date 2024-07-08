package com.example.mygrub.admin.verifications;

public class VerificationItem {

    private String key, userID, email, created_datetime;

    public VerificationItem() {
    }

    public VerificationItem(String key, String userID, String email, String created_datetime) {
        this.key = key;
        this.userID = userID;
        this.email = email;
        this.created_datetime = created_datetime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }
}
