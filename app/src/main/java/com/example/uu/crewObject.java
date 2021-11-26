package com.example.uu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class crewObject implements Parcelable {
    private String crewName;
    private String leader;
    private String Explanation;
    private String crewImage;
    private String location;
    private int totalUserNum;
    private FitTestData fitTestData;
    private Map<String, Object> userList = new HashMap<String, Object>();

    public crewObject() { }

    protected crewObject(Parcel in) {
        crewName = in.readString();
        leader = in.readString();
        Explanation = in.readString();
        crewImage = in.readString();
        location = in.readString();
        totalUserNum = in.readInt();
    }

    public static final Creator<crewObject> CREATOR = new Creator<crewObject>() {
        @Override
        public crewObject createFromParcel(Parcel in) {
            return new crewObject(in);
        }

        @Override
        public crewObject[] newArray(int size) {
            return new crewObject[size];
        }
    };

    public FitTestData getFitTestData() {
        return fitTestData;
    }

    public void setFitTestData(FitTestData fitTestData) {
        this.fitTestData = fitTestData;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getExplanation() {
        return Explanation;
    }

    public void setExplanation(String explanation) {
        Explanation = explanation;
    }

    public String getCrewImage() {
        return crewImage;
    }

    public void setCrewImage(String crewImage) {
        this.crewImage = crewImage;
    }

    public int getTotalUserNum() {
        return totalUserNum;
    }

    public void setTotalUserNum(int totalUserNum) {
        this.totalUserNum = totalUserNum;
    }

    public Map<String, Object> getUserList() {
        return userList;
    }

    public void setUserList(Map<String, Object> userList) {
        this.userList = userList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(crewName);
        dest.writeString(leader);
        dest.writeString(Explanation);
        dest.writeString(crewImage);
        dest.writeString(location);
        dest.writeInt(totalUserNum);
    }
}
