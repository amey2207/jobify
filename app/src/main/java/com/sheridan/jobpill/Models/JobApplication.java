package com.sheridan.jobpill.Models;

public class JobApplication {

    private String jobId, applicantId, applicationDate, status;

    public JobApplication(){}

    public JobApplication(String jobId, String applicantId, String applicationDate, String status) {
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
