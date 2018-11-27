package com.example.mukesh.myapplication.POJO;

import java.util.ArrayList;
import java.util.List;

public class ArtistTabAdaptorPojo {
    String heading;
    String popularity;
    String checkAtUrl;
    String followers;
    List<String> images = new ArrayList<>();

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getCheckAtUrl() {
        return checkAtUrl;
    }

    public void setCheckAtUrl(String checkAtUrl) {
        this.checkAtUrl = checkAtUrl;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
