package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UnreadMessagesCount implements Serializable {

    @SerializedName("unread_message_count")
    @Expose
    private int unreadMessageCount;

    public int getUnreadMessageCount() { return unreadMessageCount; }
    public void setUnreadMessageCount( int unreadMessageCount ) { this.unreadMessageCount = unreadMessageCount; }
}