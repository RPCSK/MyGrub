package com.example.mygrub.user.home.event;

public class EventItem {
    private String key, userID, username, imageUrl, title, startDate, endDate, type, location, qrEnabled,
            qrUrl, contact, desc;

    public EventItem() {
    }

    public EventItem(String key, String contact, String username, String userID, String imageUrl, String title, String startDate,
                     String endDate, String type, String location, String desc, String qrEnabled, String qrUrl) {
        this.key = key;
        this.userID = userID;
        this.username = username;
        this.imageUrl = imageUrl;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.desc = desc;
        this.contact = contact;
        this.location = location;
        this.qrEnabled = qrEnabled;
        this.qrUrl = qrUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQrEnabled() {
        return qrEnabled;
    }

    public void setQrEnabled(String qrEnabled) {
        this.qrEnabled = qrEnabled;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
