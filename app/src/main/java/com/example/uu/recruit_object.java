package com.example.uu;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class recruit_object {
    private String recruitId;
    private String mapUrl;
    private String leader;
    private int currentUserNum;
    private int totalUserNum;
    private String date;
    private String time;
    private String alarmTime;
    private String userInfo;
    private String hostId;
    private String runningSpeed;
    private String origin;
    private String destination;

    private ArrayList<LatLng> checkpoint= new ArrayList<>();

    public ArrayList<LatLng> getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(ArrayList<LatLng> checkpoint) {
        this.checkpoint = checkpoint;
    }

    public recruit_object() { }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    private Map<String, Object> users = new HashMap<String, Object>();


    public String getRecruitId() {
        return recruitId;
    }

    public void setRecruitId(String recruitId) {
        this.recruitId = recruitId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

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

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) { this.mapUrl=mapUrl;}

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

