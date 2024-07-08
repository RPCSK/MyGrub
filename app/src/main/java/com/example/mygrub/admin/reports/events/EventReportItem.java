package com.example.mygrub.admin.reports.events;

public class EventReportItem {
    private String created_datetime, desc, key, eventID, offenderID, reporterID, status, type;

    public EventReportItem() {
    }

    public EventReportItem(String created_datetime, String desc, String key, String eventID,
                           String offenderID, String reporterID, String status, String type) {
        this.created_datetime = created_datetime;
        this.desc = desc;
        this.key = key;
        this.eventID = eventID;
        this.offenderID = offenderID;
        this.reporterID = reporterID;
        this.status = status;
        this.type = type;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
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

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getOffenderID() {
        return offenderID;
    }

    public void setOffenderID(String offenderID) {
        this.offenderID = offenderID;
    }

    public String getReporterID() {
        return reporterID;
    }

    public void setReporterID(String reporterID) {
        this.reporterID = reporterID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
