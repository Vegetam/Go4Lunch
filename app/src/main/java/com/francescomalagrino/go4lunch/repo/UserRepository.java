package com.francescomalagrino.go4lunch.repo;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.repo.RestaurantRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class UserRepository {

    private static final String COLLECTION_USERS = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String RESERVATION_FIELD = "reservation";
    private static final String RESTAURANT_LIKED_FIELD = "restaurantLiked";
    private static final String NOTIFICATION_FIELD = "notification";
    private static final String PICTURE_FIELD = "urlPicture";
    private static final String ID_FIELD = "uid";
    private static volatile UserRepository instance;
    private static final MutableLiveData<List<User>> liveUsers = new MutableLiveData<List<User>>(){};
    private static final List<User> users = new ArrayList<>();
    private static Location location = new Location("location");
    private static User myUser;

    public UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getCurrentUserUID(){
        FirebaseUser user = getCurrentUser();
        return (user != null)? user.getUid() : null;
    }

    // Get the Collection Reference
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();
            boolean notification = true;


            User userToCreate = new User(uid, username, email, urlPicture, null,null, notification);

            Task<DocumentSnapshot> userData = getUserData();


            // If the user already exist in Firestore, we get his data
            userData.addOnSuccessListener(documentSnapshot ->
                    this.getUsersCollection().document(uid).set(userToCreate)
            );
        }
    }

    // Logout
    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    // Delete the User from Firebase
    public Task<Void> deleteUser(Context context){
        getCurrentUser().delete();
        this.getUsersCollection().document(myUser.getUid()).delete();
        return AuthUI.getInstance().delete(context);
    }

    // Get User Data from Firestore as DocumentSnapshot
    public Task<DocumentSnapshot> getUserData(){
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).get();
        }else{
            return null;
        }

    }

    // Get User Data from Firestore as a user object
    public void fetchUserData(){
        String uid = this.getCurrentUserUID();

        DocumentReference docRef = getUsersCollection().document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            myUser = documentSnapshot.toObject(User.class);
        });
    }

    // Get all users in Firestore
    public void fetchUsers(){
        String uid = this.getCurrentUserUID();
        getUsersCollection()
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult()!=null){

                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            if(uid == null){
                                continue;
                            }
                            if (uid.equals(documentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.setUsername(documentSnapshot.getString(USERNAME_FIELD));
                            user.setUrlPicture(documentSnapshot.getString(PICTURE_FIELD));
                            user.setReservation(documentSnapshot.get(RESERVATION_FIELD, Restaurant.class));
                            user.setUid(documentSnapshot.getString(ID_FIELD));
                            users.add(user);
                            setLiveUsers(users);
                        }
                    }
                });
        //.addOnSuccessListener(aVoid -> RestaurantRepository.FetchRestaurants(getLocation().getLatitude() + "," + getLocation().getLongitude()));
    }

    // Set a reservation in Firestore
    public void notificationChecked(boolean isCheck) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            this.getUsersCollection().document(uid).update(NOTIFICATION_FIELD, isCheck);
            myUser.setNotification(isCheck);
        }
    }

    // Set a reservation in Firestore
    public MutableLiveData<Boolean> addReservation(Restaurant restaurant) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        String uid = this.getCurrentUserUID();
        if(uid != null) {

            this.getUsersCollection()
                    .document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                Restaurant prevRestaurant =  Objects.requireNonNull(documentSnapshot.toObject(User.class)).getReservation();
                                if(prevRestaurant != null) {
                                    ArrayList<User> usersList = (ArrayList<User>) prevRestaurant.getHasBeenReservedBy();
                                    for(User user: usersList) {
                                        if(user.getUsername().equals(myUser.getUsername())) {
                                            usersList.remove(user);
                                        }
                                    }
                                    prevRestaurant.setHasBeenReservedBy(usersList);
                                    RestaurantRepository.getRestaurantsCollection().document(prevRestaurant.getPlaceId()).set(prevRestaurant);
                                }
                                UserRepository.this.getUsersCollection().document(uid).update(RESERVATION_FIELD, restaurant);
                                //myUser.setReservation(restaurant);
                                ArrayList<User> usersList = (ArrayList<User>) restaurant.getHasBeenReservedBy();
                                usersList.add(myUser);
                                restaurant.setHasBeenReservedBy(usersList);
                                RestaurantRepository.getRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
                                resultLiveData.setValue(true); // Indicate success

                            }

                        }
                    });


        }

        return resultLiveData;
    }

    // Add a restaurant in the favorite list in Firestore
    public void addRestaurantLiked(String restaurantLiked) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            this.getUsersCollection().document(uid).update(RESTAURANT_LIKED_FIELD, FieldValue.arrayUnion(restaurantLiked));
            myUser.getRestaurantLiked().add(restaurantLiked);
        }
    }

    // Remove a reservation in Firestore
    public void removeReservation(Restaurant restaurant) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            this.getUsersCollection().document(uid).update(RESERVATION_FIELD, null);
            myUser.setReservation(null);
            ArrayList<User> usersList = (ArrayList<User>) restaurant.getHasBeenReservedBy();
            for(User user: usersList) {
                if(user.getUsername().equals(myUser.getUsername())) {
                    usersList.remove(user);
                }
            }
            restaurant.setHasBeenReservedBy(usersList);
            RestaurantRepository.getRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
        }
    }

    // Remove a restaurant in the favorite list in Firestore
    public void removeRestaurantLiked(String name) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            this.getUsersCollection().document(uid).update(RESTAURANT_LIKED_FIELD, FieldValue.arrayRemove(name));
            myUser.getRestaurantLiked().remove(name);

        }
    }

    // Add users who reserved a restaurant in the restaurant object
    public List<Result> CrossDataUsersAndRestaurant(MutableLiveData<List<Result>> allRestaurants){
        if (liveUsers.getValue() != null&& !Objects.requireNonNull(liveUsers.getValue()).isEmpty()){
            for(int i = 0 ; i < liveUsers.getValue().size() ; i++){
                if(allRestaurants != null && !Objects.requireNonNull(allRestaurants.getValue()).isEmpty()){
                    for(int y = 0; y < allRestaurants.getValue().size() ; y++){
                        if (liveUsers.getValue().get(i).getReservation().equals(allRestaurants.getValue().get(y).getName())){
                            allRestaurants.getValue().get(y).addHasBeenReservedBy(liveUsers.getValue().get(i));
                        }
                    }
                }
            }
        }
        return Objects.requireNonNull(allRestaurants).getValue();
    }

    // Get all users who reserved this restaurant

    //---------------
    // GETTERS AND SETTERS
    //----------------

    public LiveData<List<User>> getUsers() {
        return liveUsers;
    }

    public Location getLocation() {
        Log.e("TAG" , "UserRepositoryGet.location" + location);
        return location;
    }

    public void setLocation(Location location) {
        UserRepository.location = location;
        Log.e("TAG" , "UserRepositorySet.location" + UserRepository.location + "Location" +  location);

    }

    public static MutableLiveData<List<User>> getLiveUsers() {
        return liveUsers;
    }

    public static void setLiveUsers(List<User> users) {
        liveUsers.setValue(users);
    }

    public static User getMyUser() {
        return myUser;
    }

}