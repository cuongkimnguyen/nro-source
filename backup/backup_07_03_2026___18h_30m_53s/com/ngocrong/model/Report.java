package com.ngocrong.model;

import lombok.Data;

@Data
public class Report {

    private int reporterID;
    private String reporterName;
    private int playerReportedID;
    private String playerReportedName;
    private long time;
    private String title;
    private String content;

    public Report() {
        this.time = System.currentTimeMillis();
    }

    public void save() {
    }
}
