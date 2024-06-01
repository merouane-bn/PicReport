package com.example.myapplication.Utils;

import java.util.Date;

public class Report {
    private Date date;
    private String pdfUrl;
    private String userId;

    public Report(Date date, String pdfUrl, String userId) {
        this.date = date;
        this.pdfUrl = pdfUrl;
        this.userId = userId;
    }
    public Report() {
        // Required empty public constructor
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getpdfUrl() {
        return pdfUrl;
    }

    public void setpdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
