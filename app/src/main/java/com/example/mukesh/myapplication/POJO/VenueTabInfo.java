package com.example.mukesh.myapplication.POJO;

public class VenueTabInfo {
    private String venueName;
    private String address;
    private String city;
    private String phoneNumber = "";
    private String openHours = "";
    private String generalRule = "";
    private String childRule = "";
    private String lat;
    private String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getGeneralRule() {
        return generalRule;
    }

    public void setGeneralRule(String generalRule) {
        this.generalRule = generalRule;
    }

    public String getChildRule() {
        return childRule;
    }

    public void setChildRule(String childRule) {
        this.childRule = childRule;
    }
}
