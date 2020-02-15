package com.seu.qapp.model;

import java.util.Date;

public class Message {


    private String displayname;
    private String user_id;
    private String message;
    private String message_id;
    private Date timestamp;


    public Message(String displayname, String userid, String message, Date timestamp) {
        this.displayname = displayname;
        this.message = message;
        this.user_id = userid;
        this.timestamp = timestamp;
    }

    public Message() {

    }

    public String getDisplayname() {
        return displayname;
    }


    public String getUser_id() {
        return user_id;
    }


    public String getMessage() {
        return message;
    }


    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }


    @Override
    public String toString() {
        return "ChatMessage{" +
                "user=" + displayname +
                ", message='" + message + '\'' +
                ", message_id='" + message_id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}