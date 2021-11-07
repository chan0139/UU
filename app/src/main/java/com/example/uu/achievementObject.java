package com.example.uu;

import java.util.Date;

public class achievementObject {

    private String objName;
    private String description;
    private float achievement;
    private Date clearDate;
    private boolean flag;

    public achievementObject(String objName, String description, boolean flag) {
        this.objName = objName;
        this.description = description;
        this.flag=flag;
        this.achievement=0.0f;
        this.clearDate=null;
    }

    public String getObjName() {
        return objName;
    }

    public String getDescription() {
        return description;
    }

    public float getAchievement() {
        return achievement;
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
