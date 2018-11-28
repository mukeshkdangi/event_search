package com.example.mukesh.myapplication.POJO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class EventDetails {
    public String eventName;

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    private String eventVenue;
    private String eventId;
    private String eventDate;
    private String eventType;
    private boolean isFav;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public String getEventName() {
        return eventName;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isFav() {
        return isFav;
    }
}
