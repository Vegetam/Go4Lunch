package com.francescomalagrino.go4lunch.ui.main;

import android.location.Location;

import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.repo.UserRepository;
import com.google.android.gms.tasks.Task;

public class MainViewModel extends ViewModel {

    // REPOSITORIES

    private final UserRepository userSource;

    // DATA


    // CONSTRUCTOR

    public MainViewModel(UserRepository userSource) {
        this.userSource = userSource;
    }

    // -------------
    // FOR USER
    // -------------

    public void createUser(){userSource.createUser();}

    public void setLocation(Location location) {
        userSource.setLocation(location);
    }
    public Task<User> getUserData(){
        return userSource.getUserData().continueWith(task -> task.getResult().toObject(User.class)) ;
    }
    // -------------
    // FOR RESTAURANT
    // -------------

}
