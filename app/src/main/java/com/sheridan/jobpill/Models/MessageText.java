package com.sheridan.jobpill.Models;

public class MessageText {
    private String from, message, type, to, messageID;

    public MessageText()
    {

    }

    public MessageText(String from, String message, String type, String to, String messageID) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.messageID = messageID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
