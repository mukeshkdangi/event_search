package com.example.mukesh.myapplication.POJO;

import java.lang.reflect.Field;

public class EventTabInfo {
    private String eventName;
    private String artistName;
    private String venueName;
    private String time;
    private String category;
    private String priceRange;
    private String ticketStatus;
    private String buyTicketUrl;
    private String seatMapUrl;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getBuyTicketUrl() {
        return buyTicketUrl;
    }

    public void setBuyTicketUrl(String buyTicketUrl) {
        this.buyTicketUrl = buyTicketUrl;
    }

    public String getSeatMapUrl() {
        return seatMapUrl;
    }

    public void setSeatMapUrl(String seatMapUrl) {
        this.seatMapUrl = seatMapUrl;
    }

    public int checkNull() {
        try {
            int count = 0;
            for (Field f : getClass().getDeclaredFields())
                if (f.get(this) != null)
                    count++;
            return count;
        } catch (Exception e) {

        }
        return 0;

    }
}
