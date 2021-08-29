package com.example.somachar;

import com.google.gson.annotations.SerializedName;

public class News {

    // Deserialize JSON
    @SerializedName("heading")
    private final String heading;
    @SerializedName("imagelink")
    private final String imagelink;
    @SerializedName("newslink")
    private final String newslink;
    @SerializedName("papername")
    private final String papername;
    @SerializedName("time")
    private final String time;
    @SerializedName("details")
    private final String details;

    // News class constructor
    public News(String heading, String imagelink, String newslink, String papername, String time, String details) {
        this.heading = heading;
        this.imagelink = imagelink;
        this.newslink = newslink;
        this.papername = papername;
        this.time = time;
        this.details = details;
    }

    public String getHeading() {
        return heading;
    }

    public String getImagelink() {
        return imagelink;
    }

    public String getNewslink() {
        return newslink;
    }

    public String getPapername() {
        return papername;
    }

    public String getTime() {
        return time;
    }

    public String getDetails() {
        return details;
    }
}
