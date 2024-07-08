package com.example.mygrub.admin.suspensions.all;

public class UserItem {
    private String key, reason, suspend_date, userID, email, privilege;
    private int demerits, merits;

    public UserItem() {
    }

    public UserItem(String key, String reason, String suspend_date, String userID, String email,
                    int demerits, int merits, String privilege) {
        this.key = key;
        this.reason = reason;
        this.suspend_date = suspend_date;
        this.userID = userID;
        this.email = email;
        this.demerits = demerits;
        this.merits = merits;
        this.privilege = privilege;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSuspend_date() {
        return suspend_date;
    }

    public void setSuspend_date(String suspend_date) {
        this.suspend_date = suspend_date;
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

    public int getDemerits() {
        return demerits;
    }

    public void setDemerits(int demerits) {
        this.demerits = demerits;
    }

    public int getMerits() {
        return merits;
    }

    public void setMerits(int merits) {
        this.merits = merits;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
}
