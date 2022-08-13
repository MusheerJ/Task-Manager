package com.taskmanager.horkrux.Models;

import java.io.Serializable;

public class Users implements Serializable {


    private String userName, userEmail, userPass, fireuserid, userDept, userProfile;
    final public static String ANDROID_DEPT = "Android";
    final public static String WEB_DEPT = "Web";
    final public static String UI_UX_DEPT = "UI/UX";
    final public static String MBA_DEPT = "MBA";
    final public static String NO_PROFILE = "No Profile";

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public Users(String userEmail, String userPass, String userName, String fireuserid) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPass = userPass;
        this.fireuserid = fireuserid;
    }

    public Users() {

    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String dept) {
        this.userDept = dept;
    }

    public String getFireuserid() {
        return fireuserid;
    }

    public void setFireuserid(String fireuserid) {
        this.fireuserid = fireuserid;
    }


    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
