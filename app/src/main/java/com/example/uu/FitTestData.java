package com.example.uu;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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

    public FitTestData(String crewName, int runningTime, int distance,int day, int startTime, Address startAddress, Address endAddress) {
        this.numberOfRunning = 1;
        this.crewName = crewName;
        this.runningTime = runningTime;
        this.distance = distance;
        this.day = new ArrayList<>();
        for(int i=0;i<7;i++){
            this.day.add(0);
        }
        this.day.set(day,this.day.get(day)+1);
        this.startTime = new ArrayList<>();
        for(int i=0;i<24;i++){
            this.startTime.add(0);
        }
        this.startTime.set(startTime,this.startTime.get(startTime)+1);
        this.startAddress = new ArrayList<>();
        this.startAddress.add(new LatLng(startAddress.getLatitude(),startAddress.getLongitude()));
        this.endAddress = new ArrayList<>();
        this.endAddress.add(new LatLng(endAddress.getLatitude(),endAddress.getLongitude()));
    }

    public int getNumberOfRunning() {
        return numberOfRunning;
    }

    public void setNumberOfRunning() {
        this.numberOfRunning = this.numberOfRunning+1;
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
        this.runningTime = (this.runningTime*(numberOfRunning-1)+runningTime)/numberOfRunning;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = (this.distance*(numberOfRunning-1)+distance)/numberOfRunning;
    }

    public List<Integer> getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day.set(day,this.day.get(day)+1);
    }

    public List<Integer> getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime.set(startTime,this.startTime.get(startTime)+1);
    }

    public List<LatLng> getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(Address startAddress) {
        if(this.startAddress.size()==10){
            this.startAddress.remove(0);
        }
        this.startAddress.add(new LatLng(startAddress.getLatitude(),startAddress.getLongitude()));
    }

    public List<LatLng> getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(Address endAddress) {
        if(this.endAddress.size()==10){
            this.endAddress.remove(0);
        }
        this.endAddress.add(new LatLng(endAddress.getLatitude(),endAddress.getLongitude()));
    }
}
