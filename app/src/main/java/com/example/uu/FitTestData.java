package com.example.uu;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class FitTestData {
    private int numberOfRunning;
    private String crewName;
    private int runningTime;//한번에 몇분? 1
    private int distance;// 한번에 몇미터 2
    // 0  1  2  3  4  5  6
    // 월 화 수  목 금 토 일
    private List<Integer> day;//무슨요일에 주로? 4
    //0 1 ~ 24
    private List<Integer> startTime;//운동 시작시간 5
    private List<LatLng> startAddress;  // 6
    private List<LatLng> endAddress;     // 6

    public FitTestData() {
    }

    public int getNumberOfRunning() {
        return numberOfRunning;
    }

    public void setNumberOfRunning(int numberOfRunning) {
        this.numberOfRunning = numberOfRunning;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Integer> getDay() {
        return day;
    }

    public void setDay(List<Integer> day) {
        this.day = day;
    }

    public List<Integer> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<Integer> startTime) {
        this.startTime = startTime;
    }

    public List<LatLng> getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(List<LatLng> startAddress) {
        this.startAddress = startAddress;
    }

    public List<LatLng> getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(List<LatLng> endAddress) {
        this.endAddress = endAddress;
    }
}
