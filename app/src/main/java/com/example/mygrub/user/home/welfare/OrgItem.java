package com.example.mygrub.user.home.welfare;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Map;

public class OrgItem implements Parcelable {
    private String key, address, contact, link, imageUrl;
    private Map<String, String> details;

    public OrgItem() {
    }

    public OrgItem(String key, String address, String contact, String link, String imageUrl) {
        this.key = key;
        this.address = address;
        this.contact = contact;
        this.link = link;
        this.imageUrl = imageUrl;
    }

    protected OrgItem(Parcel in) {
        key = in.readString();
        details = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<OrgItem> CREATOR = new Creator<OrgItem>() {
        @Override
        public OrgItem createFromParcel(Parcel in) {
            return new OrgItem(in);
        }

        @Override
        public OrgItem[] newArray(int size) {
            return new OrgItem[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public void setOrgDetails(){
        if (!details.isEmpty() && details != null){
            setAddress(details.get("address"));
            setContact(details.get("contact"));
            setLink(details.get("link"));
            setImageUrl(details.get("imageUrl"));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeMap(details);
    }
}
