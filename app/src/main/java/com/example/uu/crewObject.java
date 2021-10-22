package com.example.uu;

import java.util.HashMap;
import java.util.Map;

public class crewObject {
    private String crewName;
    private String leader;
    private String Explanation;
    private String crewImage;
    private int totalUserNum;
    private Map<String, Object> userList = new HashMap<String, Object>();

    public crewObject() { }

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
}
