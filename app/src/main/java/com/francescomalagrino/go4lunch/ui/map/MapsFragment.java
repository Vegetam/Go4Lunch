package com.francescomalagrino.go4lunch.ui.map;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.databinding.FragmentMapsBinding;
import com.francescomalagrino.go4lunch.ui.ViewModelFactory;
import com.francescomalagrino.go4lunch.ui.details.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {



    private FragmentMapsBinding binding;
    private MapsViewModel mViewModel;
    private SupportMapFragment mapFragment;
    protected MutableLiveData<List<Result>> restaurants ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(MapsViewModel.class);

        //mViewModel.getRestaurants().observe(getViewLifecycleOwner(), this::updateView);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mViewModel.getRestaurants().observe(getViewLifecycleOwner(),results -> this.setMarkers(results, googleMap));

        // SET LISTENER FOR MyPOSITION BUTTON
        binding.floatingActionButton.setOnClickListener(v -> moveCameraToMyPosition(googleMap));
        moveCameraToMyPosition(googleMap);

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(MapsFragment.this);
    }

    private void setMarkers(List<Restaurant> restaurants, @NonNull GoogleMap googleMap) {
        binding.progressBar.setVisibility(View.GONE);
        Log.e("TAG", "WE are HEre" + restaurants.size());
        // SET ALL MARKERS
        for(int i = 0; i < Objects.requireNonNull(restaurants).size() ; i++){
            LatLng position = new LatLng(restaurants.get(i).getLat(), restaurants.get(i).getLng());
            Marker marker;
           //if (restaurants.get(i).getClientsTodayList().size() > 0){
            //    marker = googleMap.addMarker(new MarkerOptions().position(position)
              //          .title(restaurants.get(i).getRestoName())
                //        .icon(getMarkerIcon("#22ad1f")));
                //Objects.requireNonNull(marker).setTag(i);
            //}else{
                marker = googleMap.addMarker(new MarkerOptions().position(position)
                        .title(restaurants.get(i).getRestoName()));
                Objects.requireNonNull(marker).setTag(restaurants.get(i));
           // }


        }

    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void moveCameraToMyPosition(GoogleMap googleMap) {

        LatLng myPosition = new LatLng(mViewModel.getLocation().getLatitude(),mViewModel.getLocation().getLongitude());
        Log.e("TAG" , "MyPosition : " + myPosition.latitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,14));
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.e(TAG, "onMarkerClick: "+ marker.getTag() );
        Restaurant restaurant = (Restaurant) marker.getTag();
        if (restaurant != null){
              startDetailsActivity(restaurant);
        }
        return true;
    }

    public void startDetailsActivity(Restaurant restaurant){
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("Restaurant", (Serializable) restaurant);
        startActivity(intent);
    }
}