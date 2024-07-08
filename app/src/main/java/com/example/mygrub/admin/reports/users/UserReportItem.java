package com.example.mygrub.admin.reports.users;

public class UserReportItem {
    private String key, reporterID, offenderID, offenderContact, offenderMessage,
            type, desc, created_datetime;

    public UserReportItem() {
    }

    public UserReportItem(String key, String reporterID, String offenderID, String offenderContact,
                          String offenderMessage, String type, String desc, String created_datetime) {
        this.key = key;
        this.reporterID = reporterID;
        this.offenderID = offenderID;
        this.offenderContact = offenderContact;
        this.offenderMessage = offenderMessage;
        this.type = type;
        this.desc = desc;
        this.created_datetime = created_datetime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReporterID() {
        return reporterID;
    }

    public void setReporterID(String reporterID) {
        this.reporterID = reporterID;
    }

    public String getOffenderID() {
        return offenderID;
    }

    public void setOffenderID(String offenderID) {
        this.offenderID = offenderID;
    }

    public String getOffenderContact() {
        return offenderContact;
    }

    public void setOffenderContact(String offenderContact) {
        this.offenderContact = offenderContact;
    }

    public String getOffenderMessage() {
        return offenderMessage;
    }

    public void setOffenderMessage(String offenderMessage) {
        this.offenderMessage = offenderMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
