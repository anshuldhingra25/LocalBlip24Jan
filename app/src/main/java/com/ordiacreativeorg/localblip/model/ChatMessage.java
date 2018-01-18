package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created again by Sergey Mitrofanov (goretz.m@gmail.com) on 11/26/2015
 */
public class ChatMessage implements Serializable{

    @SerializedName("senderid")
    @Expose
    private int senderId;
    @SerializedName("sendername")
    @Expose
    private String senderName;
    @SerializedName("senderemail")
    @Expose
    private String senderEmail;
    @SerializedName("receiverid")
    @Expose
    private int receiverId;
    @SerializedName("receivername")
    @Expose
    private String receiverName;
    @SerializedName("receiveremail")
    @Expose
    private String receivereMail;
    @SerializedName("messageid")
    @Expose
    private int messageId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("added")
    @Expose
    private String added;
    @SerializedName("opened")
    @Expose
    private int opened;
    @SerializedName("reply")
    @Expose
    private int reply;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("blipalert")
    @Expose
    private int blipAlert;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceivereMail() {
        return receivereMail;
    }

    public void setReceivereMail(String receivereMail) {
        this.receivereMail = receivereMail;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isBlipAlert() {
        return blipAlert == 1;
    }

    public void setBlipAlert(boolean blipAlert) {
        this.blipAlert = blipAlert ? 1 : 0;
    }
}
