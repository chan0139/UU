package com.example.uu;

public class user_Object {
    private String userId;
    private String defaultPwd;
    private String userProfileUrl;
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public user_Object() { } //빈 생성자 필수, firebase에서

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDefaultPwd() {
        return defaultPwd;
    }

    public void setDefaultPwd(String defaultPwd) {
        this.defaultPwd = defaultPwd;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }
}
