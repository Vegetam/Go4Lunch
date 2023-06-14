package com.francescomalagrino.go4lunch.data;

import android.widget.ImageView;

import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.data.details.OpeningHours;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Restaurant implements Serializable {
    private String restoName;
    private Date dateCreated;
    private String address;
    private Double lat;
    private Double lng;
    private String placeId;
    private String photoReferece;
    private Float rating;
    private String website;


    private String formattedAddress;
    private String phoneNumber;

    private List<User> hasBeenReservedBy=new ArrayList<>();

    private String vicinity;

    private OpeningHours openingHours;

    private List<String> restaurantLiked;

    public Restaurant() {}


    public Restaurant(String restoName, String address) {
        this.restoName = restoName;
        this.address = address;
        this.formattedAddress=formattedAddress;
        this.placeId=placeId;
        this.photoReferece=photoReferece;
        this.website=website;
        this.phoneNumber=phoneNumber;
        this.hasBeenReservedBy = new ArrayList<User>(hasBeenReservedBy);
    }

    // --- GETTERS ---
    public String getRestoName() {
        return restoName; }
    @ServerTimestamp
    public Date getDateCreated() {
        return dateCreated; }

    public String getAddress() { return address;}

    // --- SETTERS ---
    public void setRestoName(String restoName) { this.restoName = restoName; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
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

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getPhotoUrl() {
        return "https://maps.googleapis.com/maps/api/place/photo?key=" + BuildConfig.API_KEY+"&photo_reference="+ photoReferece + "&maxwidth=400";
    }

    public String setPhotoUrl() {
        return "";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public List<User> getHasBeenReservedBy() {
        return hasBeenReservedBy;
    }

    public void setHasBeenReservedBy(List<User> hasBeenReservedBy) {
        this.hasBeenReservedBy = hasBeenReservedBy;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<String> getRestaurantLiked() {
        return restaurantLiked;
    }

    public void setRestaurantLiked(List<String> restaurantLiked) {
        this.restaurantLiked = restaurantLiked;
    }
}