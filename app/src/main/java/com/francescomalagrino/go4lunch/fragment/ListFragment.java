package com.francescomalagrino.go4lunch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.data.ApiClient;
import com.francescomalagrino.go4lunch.data.ApiInterface;
import com.francescomalagrino.go4lunch.data.details.ListDetailResult;
import com.francescomalagrino.go4lunch.data.details.RestaurantDetailResult;
import com.francescomalagrino.go4lunch.data.nearby.GooglePlacesResult;
import com.francescomalagrino.go4lunch.repo.ListOfRestaurantsAdapter;
import com.francescomalagrino.go4lunch.utils.DisplayNearByPlaces;
import com.francescomalagrino.go4lunch.view.RestaurantDetailActivity;
import com.google.android.gms.maps.model.LatLng;
import com.francescomalagrino.go4lunch.view.LunchActivity;
import com.francescomalagrino.go4lunch.R;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment implements DisplayNearByPlaces {

    private RecyclerView mRecyclerView;

    private final static String TAG = "ListFragment" ;
    private String PLACEIDRESTO = "resto_place_id";

    private ListOfRestaurantsAdapter adapter;
    private RestaurantDetailResult mResto;
    private ArrayList<RestaurantDetailResult> listRestos = new ArrayList<>();

    private LatLng myLatLng;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = view.findViewById(R.id.fragment_restaurants_recyclerview);
        ((LunchActivity) requireActivity()).setActionBarTitle(getResources().getString(R.string.toolbar_title));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void updateNearbyPlaces(final List<GooglePlacesResult> googlePlacesResults) {

        mResto = null;
        listRestos = new ArrayList<>();

        // Call to the Google Places API
        Call<ListDetailResult> call;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        for (int i = 0; i < googlePlacesResults.size(); i++) {
            call = apiService.getRestaurantDetail(BuildConfig.API_KEY, googlePlacesResults.get(i).getPlaceId(), "name,rating,photo,url,formatted_phone_number,website,address_component,id,geometry,place_id,opening_hours");

            call.enqueue(new Callback<ListDetailResult>() {
                @Override
                public void onResponse(@NonNull Call<ListDetailResult> call, @NonNull Response<ListDetailResult> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Code: " + response.code(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onResponse: error");
                        return;
                    }

                    ListDetailResult posts = response.body();
                    if (posts != null) {
                        mResto = posts.getResult();
                        // fill the recyclerview
                        listRestos.add(mResto);

                        adapter = new ListOfRestaurantsAdapter(listRestos, Glide.with(mRecyclerView), googlePlacesResults.size(), myLatLng);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.setAdapter(adapter);

                        // Launch RestaurantDetails when user clicks on an articles item
                        adapter.setOnItemClickedListener(new ListOfRestaurantsAdapter.OnItemClickedListener() {
                            @Override
                            public void OnItemClicked(int position) {
                                Intent i = new Intent(getContext(), RestaurantDetailActivity.class);
                                i.putExtra(PLACEIDRESTO, listRestos.get(position).getPlaceId());
                                startActivity(i);
                            }
                        });
                    }

                }
                @Override
                public void onFailure(@NonNull Call<ListDetailResult> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    public void setUserLocation(LatLng userLatLng){
        myLatLng = userLatLng;
    }
}
