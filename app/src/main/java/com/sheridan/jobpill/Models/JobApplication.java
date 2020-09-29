package com.sheridan.jobpill.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class JobApplication implements Parcelable {

    private String itemId;
    private String applicantId;
    private String applicantName;
    private String applicantIntro;
    private String applicantPhone;
    private String applicantCity;
    private String applicantPhoto;
    private String applicantDateOfBirth;
    private String applicationDate;
    private String jobId;
    private String status;

    public JobApplication() {
    }

    public JobApplication(String applicantId, String applicantName, String applicantIntro, String applicantPhone, String applicantCity, String applicantPhoto,String applicantDateOfBirth, String applicationDate,String jobId, String status, String itemId) {
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.applicantIntro = applicantIntro;
        this.applicantPhone = applicantPhone;
        this.applicantCity = applicantCity;
        this.applicantPhoto = applicantPhoto;
        this.applicationDate = applicationDate;
        this.applicantDateOfBirth = applicantDateOfBirth;
        this.jobId = jobId;
        this.status = status;
        this.itemId = itemId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantIntro() {
        return applicantIntro;
    }

    public void setApplicantIntro(String applicantIntro) {
        this.applicantIntro = applicantIntro;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getApplicantCity() {
        return applicantCity;
    }

    public void setApplicantCity(String applicantCity) {
        this.applicantCity = applicantCity;
    }

    public String getApplicantPhoto() {
        return applicantPhoto;
    }

    public void setApplicantPhoto(String applicantPhoto) {
        this.applicantPhoto = applicantPhoto;
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

    public String getApplicantDateOfBirth() {
        return applicantDateOfBirth;
    }

    public void setApplicantDateOfBirth(String applicantDateOfBirth) {
        this.applicantDateOfBirth = applicantDateOfBirth;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "JobApplication{" +
                "itemId='" + itemId + '\'' +
                ", applicantId='" + applicantId + '\'' +
                ", applicantName='" + applicantName + '\'' +
                ", applicantIntro='" + applicantIntro + '\'' +
                ", applicantPhone='" + applicantPhone + '\'' +
                ", applicantCity='" + applicantCity + '\'' +
                ", applicantPhoto='" + applicantPhoto + '\'' +
                ", applicantDateOfBirth='" + applicantDateOfBirth + '\'' +
                ", applicationDate='" + applicationDate + '\'' +
                ", jobId='" + jobId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    protected JobApplication(Parcel in){
        itemId = in.readString();
        applicantId = in.readString();
        applicantName = in.readString();
        applicantCity = in.readString();
        applicantIntro = in.readString();
        applicantPhone = in.readString();
        applicantPhoto = in.readString();
        applicationDate = in.readString();
        applicantDateOfBirth = in.readString();
        jobId = in.readString();
        status = in.readString();
    }

    public static final Creator<JobApplication> CREATOR = new Creator<JobApplication>() {
        @Override
        public JobApplication createFromParcel(Parcel in) {
            return new JobApplication(in);
        }

        @Override
        public JobApplication[] newArray(int size) {
            return new JobApplication[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemId);
        parcel.writeString(applicantId);
        parcel.writeString(applicantName);
        parcel.writeString(applicantCity);
        parcel.writeString(applicantIntro);
        parcel.writeString(applicantPhone);
        parcel.writeString(applicantPhoto);
        parcel.writeString(applicationDate);
        parcel.writeString(applicantDateOfBirth);
        parcel.writeString(jobId);
        parcel.writeString(status);

    }
}
