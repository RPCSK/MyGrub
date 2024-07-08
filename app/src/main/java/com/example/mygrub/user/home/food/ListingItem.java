package com.example.mygrub.user.home.food;

import java.util.Map;

public class ListingItem {
    private String title, username, location, halalTag, dietTag0, typeTag0, key, userID, imageUrl,
    contact, desc, quantity, unit, status;
    private Map<String, String> tags;

    public ListingItem() {
    }

    public ListingItem(String title, String username, String location, String halalTag,
                       String dietTag0, String typeTag0, String key, String userID, String imageUrl,
                       String contact, String desc, String quantity, String unit, String status) {
        this.title = title;
        this.username = username;
        this.location = location;
        this.halalTag = halalTag;
        this.dietTag0 = dietTag0;
        this.typeTag0 = typeTag0;
        this.key = key;
        this.userID = userID;
        this.imageUrl = imageUrl;
        this.contact = contact;
        this.desc = desc;
        this.quantity = quantity;
        this.unit = unit;
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setHalalTag(String halalTag) {
        this.halalTag = halalTag;
    }

    public void setDietTag0(String dietTag0) {
        this.dietTag0 = dietTag0;
    }

    public void setTypeTag0(String typeTag0) {
        this.typeTag0 = typeTag0;
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

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public String getHalalTag() {
        return halalTag;
    }

    public String getDietTag0() {
        return dietTag0;
    }

    public String getTypeTag0() {
        return typeTag0;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAllTags() {
        if (!tags.isEmpty()) {
            setHalalTag(tags.get("halalTag"));
            setDietTag0(tags.get("dietTag0"));
            setTypeTag0(tags.get("typeTag0"));
        }
    }

}
