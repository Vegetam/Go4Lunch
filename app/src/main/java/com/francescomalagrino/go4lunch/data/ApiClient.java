package com.francescomalagrino.go4lunch.data;

import android.os.StrictMode;

import com.francescomalagrino.go4lunch.data.details.ListDetailResult;
import com.francescomalagrino.go4lunch.data.nearby.NearbyPlacesList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ApiClient {
    public static Retrofit getClient() {

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
