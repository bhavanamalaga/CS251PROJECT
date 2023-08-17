package com.example.socialley.Models;

public class MessageModel {

    String uId, message,messageId;
    Long timeStamp;



    String userName;

    public MessageModel(String uId,String message,Long timeStamp,String userName){
        this.uId = uId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.userName = userName;

    }

    public MessageModel(String uId, String message,String userName) {
        this.uId = uId;
        this.message = message;
        this.userName = userName;
    }
    public MessageModel(){}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}