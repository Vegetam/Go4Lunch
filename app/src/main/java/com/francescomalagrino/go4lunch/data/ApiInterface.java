package com.francescomalagrino.go4lunch.data;

import com.francescomalagrino.go4lunch.data.details.ListDetailResult;
import com.francescomalagrino.go4lunch.data.nearby.NearbyPlacesList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {



    @GET("nearbysearch/json")
    Call<NearbyPlacesList> getNearBy(@Query("location") String location,
                                     @Query("radius") int radius,
                                     @Query("type") String type,
                                     @Query("keyword") String keyword,
                                     @Query("key") String key);

    @GET("details/json")
    Call<ListDetailResult> getDetail( @Query("key")String key,
                                      @Query("place_id") String place_id,
                                      @Query("fields") String fields);

}
