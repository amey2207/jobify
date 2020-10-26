package com.sheridan.jobpill.Models;

public class Rating {

    private String itemId;
    private Float ratingScore;
    private String ratedBy;
    private String ratedByUID;
    private String postedDate;
    private String review;
    private String jobTitle;
    private String reviewerPhotoUrl;

    public Rating(){

    }

    public Rating(String itemId, Float ratingScore, String ratedBy, String ratedByUID, String postedDate, String review, String jobTitle, String reviewerPhotoUrl){
        this.itemId = itemId;
        this.ratingScore = ratingScore;
        this.ratedBy = ratedBy;
        this.ratedByUID = ratedByUID;
        this.postedDate = postedDate;
        this.review = review;
        this.jobTitle = jobTitle;
        this.reviewerPhotoUrl = reviewerPhotoUrl;
    }


    public Float getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Float ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getReviewerPhotoUrl() {
        return reviewerPhotoUrl;
    }

    public void setReviewerPhotoUrl(String reviewerPhotoUrl) {
        this.reviewerPhotoUrl = reviewerPhotoUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getRatedByUID() {
        return ratedByUID;
    }

    public void setRatedByUID(String ratedByUID) {
        this.ratedByUID = ratedByUID;
    }
}
