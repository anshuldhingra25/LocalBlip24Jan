package com.ordiacreativeorg.localblip.model;


import com.google.gson.annotations.Expose;

public class PushWooshResponse {

    public static final String MESSAGE = "message";
    public static final String BLIP_ALERT = "blip_alert";
    public static final String NEW_BLIP = "new_blip";
    public static final String BLIP_EDIT = "blip_edit";
    public static final String BLIP_EXPIRES = "blip_expires";
    @Expose
    private String title;

    @Expose
    private UserData userdata;

    public String getTitle() { return title; }
    public void setTitle( String title ) { this.title = title; }

    public UserData getUserdata() { return userdata == null ? new UserData() : userdata; }
    public void setUserdata( UserData userdata ) { this.userdata = userdata; }

    public boolean isMessage() {
        return MESSAGE.equals( getUserdata().getType() );
    }

    public boolean isBlipAlert() {
        return BLIP_ALERT.equals( getUserdata().getType() );
    }
    public boolean isBlipExpires() {
        return BLIP_EXPIRES.equals( getUserdata().getType() );
    }
    public boolean isBlipEdit() {
        return BLIP_EDIT.equals( getUserdata().getType() );
    }
    public boolean isNewBlip() {
        return NEW_BLIP.equals( getUserdata().getType() );
    }

    public String getMessage() {
        return getUserdata().getContent();
    }

    public String getSender() {
        return getUserdata().getSender_name();
    }

    public static class UserData {
        private String type;
        private String content;
        private String sender_name;

        public UserData() {}

        public String getType() { return type; }
        public void setType( String type ) { this.type = type; }

        public String getContent() { return content; }
        public void setContent( String content ) { this.content = content; }

        public String getSender_name() { return sender_name; }
        public void setSender_name( String sender_name ) { this.sender_name = sender_name; }
    }
}
