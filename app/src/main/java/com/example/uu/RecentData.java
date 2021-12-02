package com.example.uu;

public class RecentData {
    private String routeUrl;
    private String date;
    private String location;
    private String distance;
    private String time;

    public RecentData(String routeUrl, String date, String location, String distance, String time) {
        this.routeUrl = routeUrl;
        this.date = date;
        this.location = location;
        this.distance = distance;
        this.time = time;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
