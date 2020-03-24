package com.sheridan.jobpill.Models;

public class JobApplication {

    private String applicantId;
    private String applicationDate;
    private String status;

    public JobApplication(){}

    public JobApplication(String applicantId, String applicationDate, String status) {
        this.applicantId = applicantId;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
