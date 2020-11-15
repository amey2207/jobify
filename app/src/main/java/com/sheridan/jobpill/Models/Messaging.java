package com.sheridan.jobpill.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Messaging implements Parcelable {

    private String itemId;
    private String ChatJobId;
    private String ContactId;
    private String ContactName;
    private String ChatJobName;
    private String contactPhotoURL;

    public Messaging() {

    }

    public Messaging(String itemId, String chatJobId, String contactId, String contactName, String jobTitle, String contactPhotoURL) {
        this.itemId = itemId;
        this.ChatJobId = chatJobId;
        this.ContactId = contactId;
        this.ContactName = contactName;
        this.ChatJobName = jobTitle;
        this.contactPhotoURL = contactPhotoURL;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getContactPhotoURL() {
        return contactPhotoURL;
    }

    public void setContactPhotoURL(String contactPhotoURL) {
        this.contactPhotoURL = contactPhotoURL;
    }

    public String getChatJobId() {
        return ChatJobId;
    }

    public void setChatJobId(String chatJobId) {
        this.ChatJobId = chatJobId;
    }

    public String getContactId() {
        return ContactId;
    }

    public void setContactId(String contactId) {
        this.ContactId = contactId;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        this.ContactName = contactName;
    }

    public String getChatJobName() {
        return ChatJobName;
    }

    public void setChatJobName(String jobTitle) {
        this.ChatJobName = jobTitle;
    }


    protected Messaging(Parcel in) {
        itemId = in.readString();
        ChatJobId = in.readString();
        ContactId = in.readString();
        ContactName = in.readString();
        ChatJobName = in.readString();
        contactPhotoURL = in.readString();
    }

    public static final Parcelable.Creator<Messaging> CREATOR = new Parcelable.Creator<Messaging>() {
        @Override
        public Messaging createFromParcel(Parcel in) {
            return new Messaging(in);
        }

        @Override
        public Messaging[] newArray(int size) {
            return new Messaging[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemId);
        parcel.writeString(ChatJobId);
        parcel.writeString(ContactId);
        parcel.writeString(ContactName);
        parcel.writeString(ChatJobName);
        parcel.writeString(contactPhotoURL);
    }
}
