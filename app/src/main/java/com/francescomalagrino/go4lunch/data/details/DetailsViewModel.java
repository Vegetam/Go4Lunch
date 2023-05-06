package com.francescomalagrino.go4lunch.data.details;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.repo.RestaurantRepository;
import com.francescomalagrino.go4lunch.repo.UserRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class DetailsViewModel extends ViewModel {

    // REPOSITORIES

    private final UserRepository userSource;


    // DATA
    final MutableLiveData<User> user = new MutableLiveData<>();
    List<User> JoiningUsers;

    // CONSTRUCTOR

    public DetailsViewModel(UserRepository userSource, RestaurantRepository restaurantSource, Executor executor) {
        this.userSource = userSource;
    }

    public void init(String place_id, String restaurant_name) {

        //RestaurantRepository.FetchDetail(place_id);
        JoiningUsers = userSource.getJoiningUsers(restaurant_name);
        userSource.fetchUserData();
    }

    // -------------
    // FOR USER
    // -------------

    public User getMyUser(){
        return UserRepository.getMyUser();
    }

    // FETCH ALL USERS WHO HAVE RESERVED THE RESTAURANT
    public List<User> getJoiningUsers(){return this.JoiningUsers;}

    public MutableLiveData<User> getUser(){
        //user.setValue(userSource.getUserData().continueWith(task -> task.getResult().toObject(User.class)).getResult());
        return this.user;
    }

    public boolean getReservation(String restaurant){

        if(UserRepository.getMyUser() != null){
            return restaurant.equals(UserRepository.getMyUser().getReservation());
        }
        return false;
    }

    public boolean getFavoriteRestaurant(String restaurant){

        if(UserRepository.getMyUser().getRestaurantLiked() != null){
            for(int i = 0; i < UserRepository.getMyUser().getRestaurantLiked().size() ; i++){
                if (restaurant.equals(UserRepository.getMyUser().getRestaurantLiked().get(i))){
                    return true;
                }
            }
        }
        return false;
    }

    // ADD THIS RESTAURANT TO THE FAVORITE LIST IN FIRESTORE
    public void addRestaurantLiked(String restaurantLiked){
        userSource.addRestaurantLiked(restaurantLiked);
    }

    // SET THIS RESTAURANT AS RESERVED IN FIRESTORE
    public void addReservation(Restaurant reservation){
        userSource.addReservation(reservation);
    }

    // -------------
    // FOR RESTAURANT
    // -------------

    public ResultDetail getDetailRestaurant(){return RestaurantRepository.getDetailRestaurant();}

    public void removeReservation(Restaurant name) {
        userSource.removeReservation(name);
    }

    public void removeRestaurantLiked(String name) {
        userSource.removeRestaurantLiked(name);
    }
}