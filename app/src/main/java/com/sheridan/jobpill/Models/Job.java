package com.sheridan.jobpill.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Job implements Parcelable {



    private String itemId;
    private String createdBy;
    private String createdByUID;
    private String createdByName;
    private String createdByPhotoURL;
    private String createdDate;
    private Float estimatedPay;
    private String instructions;
    private String jobCategory;
    private String jobDescription;
    private String jobTitle;
    private String location;
    private String photoURL;
    private String jobStatus;
    private String hiringDate;
    private String hiredApplicant;

    public Job(String itemId, String createdBy, String createdByName,String createdByPhotoURL, String createdByUID, String createdDate, Float estimatedPay, String instructions,
               String jobCategory, String jobDescription, String jobTitle,
               String location, String photoURL, String jobStatus, String hiringDate, String hiredApplicant) {
        this.itemId = itemId;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdByUID = createdByUID;
        this.createdByPhotoURL = createdByPhotoURL;
        this.createdDate = createdDate;
        this.estimatedPay = estimatedPay;
        this.instructions = instructions;
        this.jobCategory = jobCategory;
        this.jobDescription = jobDescription;
        this.jobTitle = jobTitle;
        this.location = location;
        this.photoURL = photoURL;
        this.jobStatus = jobStatus;
        this.hiringDate = hiringDate;
        this.hiredApplicant = hiredApplicant;

    }

    public Job() {
    }

    protected Job(Parcel in) {
        itemId = in.readString();
        createdBy = in.readString();
        createdByName = in.readString();
        createdByUID = in.readString();
        createdByPhotoURL = in.readString();
        createdDate = in.readString();
        estimatedPay = in.readFloat();
        instructions = in.readString();
        jobCategory = in.readString();
        jobDescription = in.readString();
        jobTitle = in.readString();
        location = in.readString();
        photoURL = in.readString();
        jobStatus = in.readString();
        hiringDate = in.readString();
        hiredApplicant = in.readString();
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedByUID() {
        return createdByUID;
    }

    public void setCreatedByUID(String createdByUID) {
        this.createdByUID = createdByUID;
    }

    public String getCreatedByPhotoURL() {
        return createdByPhotoURL;
    }

    public void setCreatedByPhotoURL(String createdByPhotoURL) {
        this.createdByPhotoURL = createdByPhotoURL;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Float getEstimatedPay() {
        return estimatedPay;
    }

    public void setEstimatedPay(Float estimatedPay) {
        this.estimatedPay = estimatedPay;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(String hiringDate) {
        this.hiringDate = hiringDate;
    }

    public String getHiredApplicant() {
        return hiredApplicant;
    }

    public void setHiredApplicant(String hiredApplicant) {
        this.hiredApplicant = hiredApplicant;
    }

    @Override
    public String toString() {
        return "Job{" +
                "itemId='" + itemId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdByUID='" + createdByUID + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", createdByPhotoURL='" + createdByPhotoURL + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", estimatedPay=" + estimatedPay +
                ", instructions='" + instructions + '\'' +
                ", jobCategory='" + jobCategory + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", location='" + location + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", hiringDate='" + hiringDate + '\'' +
                ", hiredApplicant='" + hiredApplicant + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemId);
        parcel.writeString(createdBy);
        parcel.writeString(createdByName);
        parcel.writeString(createdByUID);
        parcel.writeString(createdByPhotoURL);
        parcel.writeString(createdDate);
        parcel.writeFloat(estimatedPay);
        parcel.writeString(instructions);
        parcel.writeString(jobCategory);
        parcel.writeString(jobDescription);
        parcel.writeString(jobTitle);
        parcel.writeString(location);
        parcel.writeString(photoURL);
        parcel.writeString(jobStatus);
        parcel.writeString(hiringDate);
        parcel.writeString(hiredApplicant);

    }
}
