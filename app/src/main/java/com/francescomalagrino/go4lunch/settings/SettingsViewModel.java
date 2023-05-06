package com.francescomalagrino.go4lunch.settings;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.repo.UserRepository;
import com.google.android.gms.tasks.Task;

public class SettingsViewModel extends ViewModel {

    // REPOSITORIES
    private final UserRepository userSource;

    // CONSTRUCTOR
    public SettingsViewModel(UserRepository userSource) {
        this.userSource = userSource;
    }

    public void init(){
        userSource.fetchUserData();
    }

    public User getMyUser(){
        return UserRepository.getMyUser();
    }

    public Task<Void> deleteUser(Context context){
        return userSource.deleteUser(context);
    }

    public void notificationChecked(boolean isCheck){
        userSource.notificationChecked(isCheck);
    }
}
