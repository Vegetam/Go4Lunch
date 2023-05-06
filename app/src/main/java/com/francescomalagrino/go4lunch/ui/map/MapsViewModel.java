package com.francescomalagrino.go4lunch.ui.map;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.repo.RestaurantRepository;
import com.francescomalagrino.go4lunch.repo.UserRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class MapsViewModel extends ViewModel {


    // REPOSITORIES

    private final UserRepository userSource;
    private final RestaurantRepository restaurantSource;
    // CONSTRUCTOR

    public MapsViewModel(UserRepository userSource, RestaurantRepository restaurantSource, Executor executor) {
        this.userSource = userSource;
        this.restaurantSource = restaurantSource;

    }

    // -------------
    // FOR USER
    // -------------


    // -------------
    // FOR RESTAURANT
    // -------------
    public LiveData<List<Restaurant>> getRestaurants() {
        String location = getLocation().getLatitude() + "," + getLocation().getLongitude();
        return restaurantSource.getRestaurants(location);
    }


    public List<Result> CrossDataUsersAndRestaurant(MutableLiveData<List<Result>> restaurants) {
        return  userSource.CrossDataUsersAndRestaurant(restaurants);
    }

    public Location getLocation() {
        return userSource.getLocation();
    }
}