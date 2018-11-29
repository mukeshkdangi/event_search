package com.example.mukesh.myapplication.POJO;

public class EventTabDataList {
    String key;
    String value;

    public boolean isSeatMapURL() {
        return isSeatMapURL;
    }

    public void setSeatMapURL(boolean seatMapURL) {
        isSeatMapURL = seatMapURL;
    }

    boolean isSeatMapURL;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}