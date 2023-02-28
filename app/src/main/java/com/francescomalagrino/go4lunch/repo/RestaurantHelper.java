package com.francescomalagrino.go4lunch.repo;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.data.ApiClient;
import com.francescomalagrino.go4lunch.data.ApiInterface;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.details.ListDetailResult;
import com.francescomalagrino.go4lunch.data.details.RestaurantDetailResult;
import com.francescomalagrino.go4lunch.data.nearby.GooglePlacesResult;
import com.francescomalagrino.go4lunch.data.nearby.NearbyPlacesList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";
    private static final String TAG = "RestaurantService";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String restoId, String restoName, String address) {
        Restaurant restaurantToCreate = new Restaurant(restoName, address);
        return RestaurantHelper.getRestaurantsCollection().document(restoId).set(restaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String restoId){
        //get nearbyPlace restaurant
        return RestaurantHelper.getRestaurantsCollection().document(restoId).get();
    }


    public LiveData<List <Restaurant>> getRestaurants(String location) {
        MutableLiveData <List <Restaurant>> result = new MutableLiveData<>();
        // get the list of resturant for firestore
        List<Restaurant> restaurants = new ArrayList<>();
        RestaurantHelper.getRestaurantsCollection()
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
                                                Log.d(TAG, "Google place result: " + gson.toJson(googlePlacesResults));
                                                for (int i=0; i < googlePlacesResults.size(); i++) {
                                                    call2 = apiService.getRestaurantDetail(BuildConfig.API_KEY, googlePlacesResults.get(i).getPlaceId(), "name,rating,photo,url,formatted_phone_number,website,address_component,id,geometry,place_id,opening_hours");
                                                    try {
                                                        RestaurantDetailResult detailResult = call2.execute().body().getResult();
                                                        Log.d(TAG, "Detail result: " + gson.toJson(detailResult));
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
                                                        restaurants.add(restaurant);
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                                // we need to save in firebase the list of resturant and than we need to send to the view
                                               for(Restaurant restaurant : restaurants) {
                                                   RestaurantHelper.getRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
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


    // --- UPDATE NAME---
    public  Task<Void> updateRestoName(String restoName, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("restoname", restoName);
    }

    // --- UPDATE TODAY'S USERS---
    public static Task<Void> updateClientsTodayList(List<String> clientsTodayList, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("clientsTodayList", clientsTodayList);
    }





}
