package com.example.mygrub.user.posts;

public class RequestItem {
    private String key, listID, imageUrl, title, location, halalTag, dietTag, typeTag, contact, desc,
    created_datetime, status, requesterID;

    public RequestItem() {
    }



    public RequestItem(String key, String listID, String imageUrl, String title, String location,
                       String halalTag, String dietTag, String typeTag, String contact, String desc,
                       String created_datetime, String requesterID, String status) {
        this.key = key;
        this.listID = listID;
        this.imageUrl = imageUrl;
        this.title = title;
        this.location = location;
        this.halalTag = halalTag;
        this.dietTag = dietTag;
        this.typeTag = typeTag;
        this.contact = contact;
        this.desc = desc;
        this.created_datetime = created_datetime;
        this.status = status;
        this.requesterID = requesterID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHalalTag() {
        return halalTag;
    }

    public void setHalalTag(String halalTag) {
        this.halalTag = halalTag;
    }

    public String getDietTag() {
        return dietTag;
    }

    public void setDietTag(String dietTag) {
        this.dietTag = dietTag;
    }

    public String getTypeTag() {
        return typeTag;
    }

    public void setTypeTag(String typeTag) {
        this.typeTag = typeTag;
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

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
