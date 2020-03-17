package com.sheridan.jobpill.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Job implements Parcelable {



    private String itemId;
    private String createdBy;
    private String createdDate;
    private long estimatedPay;
    private String instructions;
    private String jobCategory;
    private String jobDescription;
    private String jobTitle;
    private String location;
    private String photoURL;
    private String jobStatus;
    private ArrayList<String> usersApplied;

    public Job(String itemId, String createdBy, String createdDate, long estimatedPay, String instructions, String jobCategory, String jobDescription, String jobTitle, String location, String photoURL,String jobStatus, ArrayList<String> usersApplied) {
        this.itemId = itemId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.estimatedPay = estimatedPay;
        this.instructions = instructions;
        this.jobCategory = jobCategory;
        this.jobDescription = jobDescription;
        this.jobTitle = jobTitle;
        this.location = location;
        this.photoURL = photoURL;
        this.jobStatus = jobStatus;
        this.usersApplied = usersApplied;

    }

    public Job() {
    }

    protected Job(Parcel in) {
        itemId = in.readString();
        createdBy = in.readString();
        createdDate = in.readString();
        estimatedPay = in.readLong();
        instructions = in.readString();
        jobCategory = in.readString();
        jobDescription = in.readString();
        jobTitle = in.readString();
        location = in.readString();
        photoURL = in.readString();
        jobStatus = in.readString();
        usersApplied = in.createStringArrayList();
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public long getEstimatedPay() {
        return estimatedPay;
    }

    public void setEstimatedPay(long estimatedPay) {
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

    public ArrayList<String> getUsersApplied() {
        return usersApplied;
    }

    public void setUsersApplied(ArrayList<String> usersApplied) {
        this.usersApplied = usersApplied;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public String toString() {
        return "Job{" +
                "itemId='" + itemId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", estimatedPay=" + estimatedPay +
                ", instructions='" + instructions + '\'' +
                ", jobCategory='" + jobCategory + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", location='" + location + '\'' +
                ", photoUrl='" + photoURL + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", usersApplied=" + usersApplied +
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
        parcel.writeString(createdDate);
        parcel.writeLong(estimatedPay);
        parcel.writeString(instructions);
        parcel.writeString(jobCategory);
        parcel.writeString(jobDescription);
        parcel.writeString(jobTitle);
        parcel.writeString(location);
        parcel.writeString(photoURL);
        parcel.writeString(jobStatus);
        parcel.writeStringList(usersApplied);
    }
}
