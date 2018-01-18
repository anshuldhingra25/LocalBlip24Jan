package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dmytrobohachevskyy on 9/25/15.
 * <p>
 * Model for member
 */
public class MemberDetail {


    @SerializedName("businessdesc")
    @Expose
    private String businessdesc;


    @SerializedName("aid")
    @Expose
    private int memberId;

    @SerializedName("package")
    @Expose
    private int memberType;

    @SerializedName("apikey")
    @Expose
    private String apiKey;

    @SerializedName("firstname")
    @Expose
    private String firstName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("zipcode")
    @Expose
    private String zipCode;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("zipcodeashome")
    @Expose
    private String zipCodeAsHome;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("age")
    @Expose
    private String age;

    @SerializedName("locations")
    @Expose
    private List<Location> locations;

    @SerializedName("notifications")
    @Expose
    private List<Integer> notifications;

    @SerializedName("notifydistance")
    @Expose
    private String notifyDistance;

    @SerializedName("response")
    @Expose
    private String response;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBusinessdesc() {
        return businessdesc;
    }

    public void setBusinessdesc(String businessdesc) {
        this.businessdesc = businessdesc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String isZipCodeAsHome() {
        return zipCodeAsHome;
    }

    public void setZipCodeAsHome(String zipCodeAsHome) {
        this.zipCodeAsHome = zipCodeAsHome;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Location getLocationById(int id) {
        for (Location location : locations) {
            if (location.getLocationId() == id) {
                return location;
            }
        }
        return null;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Integer> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Integer> notifications) {
        this.notifications = notifications;
    }

    public String getNotifyDistance() {
        return notifyDistance;
    }

    public void setNotifyDistance(String notifyDistance) {
        this.notifyDistance = notifyDistance;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Convert object to string
     */
    public String toString() {
        return String.format("apikey: %s\n firstname: %s\n email: %s\n package %d\n", apiKey, firstName, email, memberType);
    }

}
