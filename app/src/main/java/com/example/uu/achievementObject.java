package com.example.uu;

import java.util.Date;

public class achievementObject {

    private String objName;
    private String description;
    private float achievement;
    private Date clearDate;

    public achievementObject(String objName, String description) {
        this.objName = objName;
        this.description = description;
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

}
