package com.example.uu;

import android.os.Parcel;
import android.os.Parcelable;

public class FitTestData implements Parcelable {
    private int distanceAverage;
    private String crewName;

    protected FitTestData(Parcel in) {
        this.distanceAverage=in.readInt();
        this.crewName=in.readString();
    }
    public FitTestData(int distanceAverage,String crewName){
        this.distanceAverage=distanceAverage;
        this.crewName=crewName;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(distanceAverage);
        parcel.writeString(crewName);
    }

    public int getDistanceAverage() {
        return distanceAverage;
    }

    public void setDistanceAverage(int distanceAverage) {
        this.distanceAverage = distanceAverage;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public static final Creator<FitTestData> CREATOR = new Creator<FitTestData>() {
        @Override
        public FitTestData createFromParcel(Parcel in) {
            return new FitTestData(in);
        }

        @Override
        public FitTestData[] newArray(int size) {
            return new FitTestData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
