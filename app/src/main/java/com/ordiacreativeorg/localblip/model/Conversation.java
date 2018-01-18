package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/26/2015
 */
public class Conversation implements Serializable{

    @SerializedName("sender_name")
    @Expose
    private String senderName;

    @SerializedName("receiver_name")
    @Expose
    private String receiverName;

    @SerializedName( "most_recent_message_datetime" )
    @Expose
    private String time;

    @SerializedName("location")
    @Expose
    private String senderLocation;
    @SerializedName("message_id")
    @Expose
    private int messageId;

    @SerializedName("sender_email")
    @Expose
    private String senderEmail;

    @SerializedName("receiver_email")
    @Expose
    private String receiverEmail;

    @SerializedName("message")
    @Expose
    private String content;

    @SerializedName("opened")
    @Expose
    private int opened;
    @SerializedName("is_reply")
    @Expose
    private int reply;

    @SerializedName("has_been_read")
    @Expose
    private int read;

    @SerializedName("blip_alert")
    @Expose
    private int blipAlert;

    @SerializedName("thread_id")
    @Expose
    private int threadId;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(String senderLocation) {
        this.senderLocation = senderLocation;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOpened() {
        return opened == 1;
    }

    public void setOpened(boolean opened) {
        this.opened = opened ? 1 : 0;
    }

    public boolean isReply() {
        return reply == 1;
    }

    public void setReply(boolean reply) {
        this.reply = reply ? 1 : 0;
    }

    public int getRead() { return read; }

    public void setRead( int read ) { this.read = read; }

    public boolean isBlipAlert() {
        return blipAlert == 1;
    }

    public void setBlipAlert(boolean blipAlert) {
        this.blipAlert = blipAlert ? 1 : 0;
    }

    public String getReceiverName() { return receiverName; }

    public void setReceiverName( String receiverName ) { this.receiverName = receiverName; }

    public String getReceiverEmail() { return receiverEmail; }

    public void setReceiverEmail( String receiverEmail ) { this.receiverEmail = receiverEmail; }

    public String getTime() { return time; }

    public void setTime( String time ) { this.time = time; }

    public int getThreadId() { return threadId; }

    public void setThreadId( int threadId ) { this.threadId = threadId; }
}
