package com.francescomalagrino.go4lunch.ui.list;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.repo.RestaurantRepository;
import com.francescomalagrino.go4lunch.repo.UserRepository;

import java.util.List;

public class ListViewModel extends ViewModel {

    // REPOSITORIES

    private final UserRepository userSource;
    private final RestaurantRepository restoSource;
    // DATA


    // CONSTRUCTOR

    public ListViewModel(UserRepository userSource, RestaurantRepository restoSource) {
        this.userSource = userSource;
        this.restoSource = restoSource;
    }



    // -------------
    // FOR USER
    // -------------

    public Location getLocation() {
        return userSource.getLocation();
    }

    // -------------
    // FOR RESTAURANT
    // -------------

    public LiveData<List<Restaurant>> getRestaurants() {
        String location = getLocation().getLatitude() + "," + getLocation().getLongitude();
        return restoSource.getRestaurants(location);
    }
}