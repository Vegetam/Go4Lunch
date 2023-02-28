package com.francescomalagrino.go4lunch.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.GooglePlacesResult;
import com.francescomalagrino.go4lunch.repo.RestaurantHelper;
import com.francescomalagrino.go4lunch.utils.DisplayNearByPlaces;
import com.francescomalagrino.go4lunch.utils.LunchDateFormat;
import com.francescomalagrino.go4lunch.view.RestaurantDetailActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.francescomalagrino.go4lunch.R;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapFragment extends Fragment implements OnMapReadyCallback, DisplayNearByPlaces {

    private View mView;

    private final static String TAG = "MapFragment";

    private RestaurantHelper mRestaurantHelper;

    @BindView(R.id.ic_gps)
    ImageView mGps;

    private Marker myMarker;
    private GoogleMap mMap;
    private double myLatitude;
    private double myLongitude;
    private String today;
    private static final float DEFAULT_ZOOM = 16f;

    private static final int Request_code = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MutableLiveData<Location> currentLocation=new MutableLiveData<>();
    private double lag, lng;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);

        LunchDateFormat forToday = new LunchDateFormat();
        today = forToday.getTodayDate();
        ButterKnife.bind(this, mView);
        // Default position
        myLatitude = 41.1135683;
        myLongitude = 16.8686615;
        //getCurrentLocation();
        mRestaurantHelper = new RestaurantHelper();

        return mView;
    }

    // --------------------
    // Update nearByPlace
    // --------------------
    @Override
    public void updateNearbyPlaces(List<GooglePlacesResult> googlePlacesResults) {
        //List<GooglePlacesResult> placesToShowId;
        //placesToShowId = googlePlacesResults;
        //displayNearbyPlaces(placesToShowId);
    }

    // --------------------
    // SetUserLocation
    // --------------------

    public void setUserLocation(LatLng userLatLng) {
        // update location
        myLatitude = userLatLng.latitude;
        myLongitude = userLatLng.longitude;

        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
        }
    }

    public final LatLng getLatLngFromAdresse(String adresse) {
        Geocoder geocoder=new Geocoder(getContext());
        LatLng latlng = null;
        try {
            List<Address> adr=  geocoder.getFromLocationName(adresse,1);
            double lat=adr.get(0).getLatitude();
            double lng=adr.get(0).getLongitude();
            latlng= new LatLng(lat, lng);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latlng;
    }
  /*
    public Location getCurrentLocation(){
        //CHeck PERMISSION if it's not GRANTED
        if(ActivityCompat.checkSelfPermission(
               getContext(), Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (getContext(),Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);
        }
        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval((5000));
        LocationCallback locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationRequest==null){
                    Log.e("currentlocation", "locationResult is null");
                }
                for (Location location:locationResult.getLocations() ){
                    if(location!=null){
                        currentLocation.postValue(location);
                        //  viewModel.setCurrentLocation(currentLocation);
                        Log.e("current Location", "onLocationResult:list resto fragement "+location.getLongitude()+" Latitude "+location.getLatitude());
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
        Task<Location> task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(getActivity(), location -> {
            if(location!=null){
                lag=location .getLatitude();
                lng=location.getLongitude();
                currentLocation.postValue(location);
                LatLng latLng=new LatLng(lag,
                        lng);
                mMap.addMarker(
                        new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .title("You are here!," +latLng.toString()));
                System.out.println("getLatitude="+lag+"et " +
                        "getLongitude="+lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        });
        Location location = new Location("service Provider");
        location.setLongitude(lng);
        location.setLatitude(lag);
        currentLocation.postValue(location);
        return location;
    }
*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap();
        clickGps();
    }

    // --------------------
    // Map of the world
    // --------------------
    //initializing map
    private void initMap() {
        MapView mMapView;
        mMapView = mView.findViewById(R.id.map);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext()  , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
        // get Lst of resturant and pass latitude and longitude
        String location1 = myLatitude + "," + myLongitude;
        mRestaurantHelper.getRestaurants(location1).observe(this, restaurants -> {
            for(Restaurant restaurant : restaurants)  {
                final MarkerOptions markerOptions = new MarkerOptions();

                // By default we put red pins
                LatLng location = new LatLng(restaurant.getLat(), restaurant.getLng());
                BitmapDescriptor icon;
              //  if(restaurant.getClientsTodayList().isEmpty()) {
                if(restaurant.getClientsTodayList() == null || restaurant.getClientsTodayList().isEmpty() ) {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange);
                }else {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_green);
                }
                markerOptions.position(location)
                        .title(restaurant.getRestoName())
                        .icon(icon);
                myMarker = mMap.addMarker(markerOptions);
                myMarker.setTag(restaurant.getPlaceId());

            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                restaurantDetail(marker);
            }
        });
    }

    // --------------------
    // OnClick for the GBS icon
    // --------------------
    private void clickGps() {
        // click on gps
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), DEFAULT_ZOOM));
            }
        });
    }

    private void displayNearbyPlaces(List<GooglePlacesResult> idRestaurant) {
        for (int i = 0; i < idRestaurant.size(); i++) {
            GooglePlacesResult restaurantLunch = idRestaurant.get(i);
            String restaurantName = restaurantLunch.getName();
            double restaurantLat = restaurantLunch.getGeometry().getLocation().getLat();
            double restaurantLng = restaurantLunch.getGeometry().getLocation().getLng();
            String restaurantPlaceId = restaurantLunch.getPlaceId();

            LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
            updateLikeColorPin(restaurantPlaceId, restaurantName, restaurantLatLng);

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    restaurantDetail(marker);
                }
            });
            updateLikeColorPin(restaurantPlaceId, restaurantName, restaurantLatLng);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    restaurantDetail(marker);
                }
            });
        }
    }

    //---------------------------------------------------------------------------------------------------
    // Update Pin color from Firebase for selected lunch
    //---------------------------------------------------------------------------------------------------
    private void updateLikeColorPin(final String placeId, final String name, final LatLng latLng) {

        // The color of the pin is adjusted according to the user's choice
        final MarkerOptions markerOptions = new MarkerOptions();

        // By default we put red pins
        markerOptions.position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange));
        myMarker = mMap.addMarker(markerOptions);
        myMarker.setTag(placeId);


        RestaurantHelper.getRestaurant(placeId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                    Date dateRestaurantSheet;
                    if (restaurant != null) {
                        dateRestaurantSheet = restaurant.getDateCreated();
                        LunchDateFormat myDate = new LunchDateFormat();
                        String dateRegistered = myDate.getRegisteredDate(dateRestaurantSheet);

                        if (dateRegistered.equals(today)) {
                            int lunchUsers = restaurant.getClientsTodayList().size();
                            if (lunchUsers > 0) {
                                markerOptions.position(latLng)
                                        .title(name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_green));
                                myMarker = mMap.addMarker(markerOptions);
                                myMarker.setTag(placeId);
                            }
                        }
                    }
                }
            }
        });
    }

    //--------------------------------------------------------------------------------------------------------------------
    //manages the launch of restaurant detail activity
    //--------------------------------------------------------------------------------------------------------------------
    //launch restaurant detail activity
    private void restaurantDetail(Marker marker) {
        String PLACE_ID_RESTAURANT = "resto_place_id";
        String ref = (String) marker.getTag();
        Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
        //Id
        intent.putExtra(PLACE_ID_RESTAURANT, ref);
        startActivity(intent);
    }
}
