package com.example.uu;

public class recruit_object {
    private String mapUrl;
    private String leader;
    private int currentUserNum;
    private int totalUserNum;
    private String date;
    private String time;
    private String userInfo;
    private String runningSpeed;

    public int getCurrentUserNum() {
        return currentUserNum;
    }

    public void setCurrentUserNum(int currentUserNum) {
        this.currentUserNum = currentUserNum;
    }

    public int getTotalUserNum() {
        return totalUserNum;
    }

    public void setTotalUserNum(int totalUserNum) {
        this.totalUserNum = totalUserNum;
    }

    public recruit_object() { }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getRunningSpeed() {
        return runningSpeed;
    }

    public void setRunningSpeed(String runningSpeed) {
        this.runningSpeed = runningSpeed;
    }
}
