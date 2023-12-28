package com.example.kuantoganha;

import androidx.annotation.NonNull;

public class Job {
    private String title;
    private String district;
    private String description;
    private String startDate;
    private String endDate;

    public Job(String title, String district, String description, String startDate, String endDate) {
        this.title = title;
        this.district = district;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDistrict() { return district; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDistrict(String district) { this.district = district; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}