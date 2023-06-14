package com.francescomalagrino.go4lunch.repo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.data.ApiClient;
import com.francescomalagrino.go4lunch.data.ApiInterface;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.details.ListDetailResult;
import com.francescomalagrino.go4lunch.data.details.RestaurantDetailResult;
import com.francescomalagrino.go4lunch.data.details.ResultDetail;
import com.francescomalagrino.go4lunch.data.nearby.GooglePlacesResult;
import com.francescomalagrino.go4lunch.data.nearby.NearbyPlacesList;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    //  DATA

    private static volatile RestaurantRepository instance;
    private static final UserRepository userRepository = UserRepository.getInstance();
    protected static Disposable disposable;
    private static final MutableLiveData<List<Result>> myRestaurants = new MutableLiveData<>();
    public static ResultDetail detailRestaurant;

    private static final String COLLECTION_NAME = "restaurants";
    private static final String TAG = "RestaurantService";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CONSTRUCTOR

    public RestaurantRepository() { }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }


    public LiveData<List <Restaurant>> getRestaurants(String location) {
        MutableLiveData <List <Restaurant>> result = new MutableLiveData<>();
        // get the list of resturant for firestore
        List<Restaurant> restaurants = new ArrayList<>();
        RestaurantRepository.getRestaurantsCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                restaurants.add(document.toObject(Restaurant.class));
                            }
                            if(restaurants.isEmpty()) {
                                //if the list is empty than call Retrofit call of NearbyPlace
                                Call<NearbyPlacesList> call;
                                String key = BuildConfig.API_KEY;
                                String keyword = "";
                                int radius = 500;

                                String type = "restaurant";
                                ApiInterface googleMapService = ApiClient.getClient().create(ApiInterface.class);
                                call = googleMapService.getNearBy(location, radius, type, keyword, key);
                                call.enqueue(new Callback<NearbyPlacesList>() {
                                    @Override
                                    public void onResponse(@NonNull Call<NearbyPlacesList> call, @NonNull Response<NearbyPlacesList> response) {
                                        if (response.isSuccessful()) {
                                            if (response.body() != null) {
                                                List<GooglePlacesResult> googlePlacesResults;
                                                googlePlacesResults = response.body().getResults();
                                                Call<ListDetailResult> call2;
                                                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                                                Gson gson = new Gson();
                                                Log.e(TAG, "Google place result: " + gson.toJson(googlePlacesResults));
                                                for (int i=0; i < googlePlacesResults.size(); i++) {
                                                    call2 = apiService.getDetail(BuildConfig.API_KEY, googlePlacesResults.get(i).getPlaceId(), "name,rating,photo,url,formatted_phone_number,website,address_component,id,geometry,place_id,opening_hours,formatted_address");
                                                    try {
                                                        RestaurantDetailResult detailResult = call2.execute().body().getResult();
                                                        Log.e(TAG, "Detail result: " + gson.toJson(detailResult));
                                                        Restaurant restaurant = new Restaurant();
                                                        restaurant.setRestoName(detailResult.getName());
                                                        restaurant.setAddress(detailResult.getFormattedAddress());
                                                        if(detailResult.getPhotos() != null && !detailResult.getPhotos().isEmpty()) {
                                                            restaurant.setPhotoReferece(detailResult.getPhotos().get(0).getPhotoReference());
                                                        }
                                                        restaurant.setPlaceId(detailResult.getPlaceId());
                                                        restaurant.setRating(detailResult.getRating());
                                                        restaurant.setWebsite(detailResult.getWebsite());
                                                        restaurant.setLat(detailResult.getGeometry().getLocation().getLat());
                                                        restaurant.setLng(detailResult.getGeometry().getLocation().getLng());
                                                        restaurant.setPhoneNumber(detailResult.getFormattedPhoneNumber());
                                                        restaurant.setOpeningHours(detailResult.getOpeninghours());
                                                        restaurant.setHasBeenReservedBy(new ArrayList<>());
                                                        restaurant.setRestaurantLiked(new ArrayList<>());
                                                        restaurants.add(restaurant);
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                                // we need to save in firebase the list of resturant and than we need to send to the view
                                                for(Restaurant restaurant : restaurants) {
                                                    RestaurantRepository.getRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
                                                }
                                                result.postValue(restaurants);

                                            }
                                        } else {
                                            //should be an error
                                            Log.d(TAG, "Error on NearBy Place response");

                                        }
                                    }
                                    @Override
                                    public void onFailure(@NonNull Call<NearbyPlacesList> call, @NonNull Throwable t) {
                                        Log.d(TAG, "Failure on NearBy Place response");
                                    }
                                });


                            }else {
                                result.postValue(restaurants);

                            }
                        } else {
                            Log.d(TAG, "Firebase Error");
                        }
                    }
                });






        //return the list.
        return result;
    }


    // GETTER AND SETTER

    public static Result getSpecificRestaurant(String name){
        for(int i = 0; i < Objects.requireNonNull(myRestaurants.getValue()).size(); i++){
            if (myRestaurants.getValue().get(i).getName().equals(name)){
                return myRestaurants.getValue().get(i);
            }
        }
        return null;
    }

    public static String getAddressRestaurant(String name){
        for(int i = 0; i < Objects.requireNonNull(myRestaurants.getValue()).size(); i++){
            if (myRestaurants.getValue().get(i).getName().equals(name)){
                return myRestaurants.getValue().get(i).getVicinity();
            }
        }
        return null;
    }



    public static ResultDetail getDetailRestaurant() {
        return detailRestaurant;
    }

    public static void setDetailRestaurant(ResultDetail detailRestaurant) {
        RestaurantRepository.detailRestaurant = detailRestaurant;
    }


}