package com.francescomalagrino.go4lunch.workmates;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.repo.UserRepository;

import java.util.List;

public class WorkmatesViewModel extends ViewModel {


    // REPOSITORIES

    private final UserRepository userSource;

    // DATA


    // CONSTRUCTOR

    public WorkmatesViewModel(UserRepository userSource) {
        this.userSource = userSource;
    }

    // -------------
    // FOR USER
    // -------------

    public MutableLiveData<List<User>> getUsers() {
        return UserRepository.getLiveUsers();
    }


}
