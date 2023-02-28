package com.francescomalagrino.go4lunch.data;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String uid;
    private String username;

    private String restoToday;
    private String restoTodayName;
    private String restoDate;
    private Boolean isWorkMates;
    @Nullable
    private String urlPicture;
    private List<String> restoLike;

    public User() {
    }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.restoToday = "";
        this.restoTodayName = "";
        this.restoDate = "";
        this.urlPicture = urlPicture;
        this.restoLike = new ArrayList<>();
        this.isWorkMates = false;


    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getIsWorkMates() {
        return isWorkMates;
    }


    //in only today not all the date! so we can keep today but i can remove the restaurant if the date change and can show a toast like You have not eat yet or no restaurant selected
    public String getRestoToday() {
        return restoToday;
    }

    public String getRestoTodayName() {
        return restoTodayName;
    }

    public String getRestoDate() {
        return restoDate;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public List<String> getRestoLike() {
        return restoLike;
    }
    // --- SETTERS ---

    public void setIsWorkMates(Boolean workMates) {
        isWorkMates = workMates;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRestoToday(String restoToday) {
        this.restoToday = restoToday;
    }

    public void setRestoTodayName(String restoTodayName) {
        this.restoTodayName = restoTodayName;
    }

    public void setRestoDate(String restoDate) {
        this.restoDate = restoDate;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setRestoLike(List<String> restoLike) {
        this.restoLike = restoLike;
    }
}