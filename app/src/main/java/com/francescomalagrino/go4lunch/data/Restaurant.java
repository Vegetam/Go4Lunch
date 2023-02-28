package com.francescomalagrino.go4lunch.data;

import android.widget.ImageView;

import com.francescomalagrino.go4lunch.BuildConfig;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Restaurant {
    private String restoName;
    private Date dateCreated;
    private String address;
    private Double lat;
    private Double lng;
    private String placeId;
    private String photoReferece;
    private Float rating;
    private String website;

    private List<String> clientsTodayList;

    public Restaurant() {}

    public Restaurant(String restoName, String address) {
        this.restoName = restoName;
        this.clientsTodayList = new ArrayList<>();
        this.address = address;
        this.lat= lat;
        this.lng=lng;
        this.placeId=placeId;
        this.photoReferece=photoReferece;
        this.rating=rating;
        this.website=website;
    }

    // --- GETTERS ---
    public String getRestoName() {
        return restoName; }
    @ServerTimestamp
    public Date getDateCreated() {
        return dateCreated; }
    public List<String> getClientsTodayList() {
        return clientsTodayList; }
    public String getAddress() { return address;}

    // --- SETTERS ---
    public void setRestoName(String restoName) { this.restoName = restoName; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setClientsTodayList(List<String> clientsTodayList) { this.clientsTodayList = clientsTodayList; }
    public void setAddress(String address) {this.address = address;}

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPhotoReferece() {
        return photoReferece;
    }

    public void setPhotoReferece(String photoReferece) {
        this.photoReferece = photoReferece;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhotoUrl() {
        return "https://maps.googleapis.com/maps/api/place/photo?key=" + BuildConfig.API_KEY+"&photo_reference="+ photoReferece + "&maxwidth=400";
    }
}