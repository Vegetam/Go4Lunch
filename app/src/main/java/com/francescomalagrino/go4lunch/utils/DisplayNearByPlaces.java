package com.francescomalagrino.go4lunch.utils;

import com.francescomalagrino.go4lunch.data.nearby.GooglePlacesResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface DisplayNearByPlaces {
    void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults);

    void setUserLocation(LatLng userLatLng);
}

