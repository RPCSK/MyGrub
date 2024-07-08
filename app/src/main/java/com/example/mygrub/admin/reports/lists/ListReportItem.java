package com.example.mygrub.admin.reports.lists;

public class ListReportItem {
    private String created_datetime, desc, key, listID, offenderID, reporterID, status, type;

    public ListReportItem() {
    }

    public ListReportItem(String created_datetime, String desc, String key, String listID,
                          String offenderID, String reporterID, String status, String type) {
        this.created_datetime = created_datetime;
        this.desc = desc;
        this.key = key;
        this.listID = listID;
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

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
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
