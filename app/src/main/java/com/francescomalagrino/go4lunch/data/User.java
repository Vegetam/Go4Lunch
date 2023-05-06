package com.francescomalagrino.go4lunch.data;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("reservation")
    @Expose
    private Restaurant restaurant;
    @SerializedName("restaurantLiked")
    @Expose
    private List<String> restaurantLiked;
    @SerializedName("urlPicture")
    @Expose
    @Nullable
    private String urlPicture;
    @SerializedName("notification")
    @Expose
    private boolean notification;

    public User() {
    }

    public User(String uid, String username, String email, @Nullable String urlPicture, Restaurant restaurant, List<String> restaurantLiked, boolean notification) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
        this.restaurant = restaurant;
        this.restaurantLiked = restaurantLiked;
        this.notification = notification;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public Restaurant getReservation() {
        return restaurant;
    }

    public void setReservation(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<String> getRestaurantLiked() {
        return restaurantLiked;
    }

    public void setRestaurantLiked(List<String> restaurantLiked) {
        this.restaurantLiked = restaurantLiked;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
