package com.example.uu;

import java.util.Date;

public class achievementObject {

    private String objName;
    private String description;
    private float achievement;
    private Date clearDate;
    private boolean flag;

    public achievementObject(String objName, String description, boolean flag,float percentage) {
        this.objName = objName;
        this.description = description;
        this.flag=flag;     //달성 목표 확인용 변수
        this.achievement=percentage;
        this.clearDate=null;
    }

    public String getObjName() {
        return objName;
    }

    public String getDescription() {
        return description;
    }

    public int getAchievement() {
        return (int)achievement;
    }

    public Date getClearDate() {
        return clearDate;
    }

    public boolean getFlag() { return flag; }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAchievement(float achievement) {
        this.achievement = achievement;
    }

    public void setClearDate(Date clearDate) {
        this.clearDate = clearDate;
    }

    public void setFlag(boolean flag) {this.flag = flag; }
}
