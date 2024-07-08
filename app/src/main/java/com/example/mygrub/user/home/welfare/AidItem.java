package com.example.mygrub.user.home.welfare;

import java.util.Map;

public class AidItem{
    private String key, imageUrl, link;
    private Map<String, String> rules, details;

    public AidItem() {
    }

    public AidItem(String key, String imageUrl, String link, Map<String, String> rules, Map<String, String> details) {
        this.key = key;
        this.imageUrl = imageUrl;
        this.link = link;
        this.rules = rules;
        this.details = details;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public void setAidDetails(){
        if (!details.isEmpty()){
            setImageUrl(details.get("imageUrl"));
            setLink(details.get("link"));
        }
    }

}
