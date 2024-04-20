package com.example.smsapp;

public class SmsMessage {
    private String sender;
    private String date; //timestamp
    private String message;
    private int type; // 1 = otrzymane, 2 = wysłane



    public SmsMessage(String sender, String date, String message, int type) {
        this.sender = sender;
        this.date = date;
        this.message = message;
    this.type=  type;
    }
    public String getSender() {
        return sender;
    }
    public String getDate() {
        return date;
    }
    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSent() {
        return type== 2;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}