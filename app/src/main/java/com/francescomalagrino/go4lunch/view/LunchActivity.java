package com.francescomalagrino.go4lunch.view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.data.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.ApiClient;
import com.francescomalagrino.go4lunch.data.ApiInterface;

import com.francescomalagrino.go4lunch.repo.UserRepository;
import com.francescomalagrino.go4lunch.fragment.ListFragment;
import com.francescomalagrino.go4lunch.fragment.MapFragment;
import com.francescomalagrino.go4lunch.data.nearby.GooglePlacesResult;
import com.francescomalagrino.go4lunch.data.nearby.NearbyPlacesList;
import com.francescomalagrino.go4lunch.workmates_chat.ChatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LunchActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final MapFragment fragment1 = new MapFragment();
    final ListFragment fragment2 = new ListFragment();
    final WorkmatesFragment fragment3 = new WorkmatesFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private String PLACE_ID_RESTAURANT = "resto_place_id";

    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String RADIUS_PREFS = "radiusForSearch";
    public static final String TYPE_PREFS = "typeOfSearch";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;

    private static final String TAG = "LunchActivity";

    public Location getCurrentLocation() {
        return currentLocation;
    }

    private Location currentLocation;


    private NavigationView navigationView;
    private List<GooglePlacesResult> results;
    private Context mContext;
    private int nav_click = 0;


    private boolean mLocationPermissionGranted = false;

    private int radius;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lunch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.lunch_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.lunch_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.lunch_container, fragment1, "1").commit();
        configureNavigationView();

        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);

        setRadiusPrefs();
        getLocationPermission();
    }

    public void setActionBarTitle(String bibi) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(bibi);
    }

    private void setRadiusPrefs() {
        Log.d(TAG, "loadPrefs: ");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String radiusString = sharedPreferences.getString(RADIUS_PREFS, "500");
        if (radiusString != null) {
            radius = Integer.parseInt(radiusString);
        }
        type = sharedPreferences.getString(TYPE_PREFS, "restaurant");
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_lunch;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ------------------------------------------------
    //Autocomplete display not for workmates fragment
    // -----------------------------------------------
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (nav_click == 3) {
            menu.removeItem(R.id.menu_activity_main_search);
        } else {
            if (menu.findItem(R.id.menu_activity_main_search) == null) {
                menu.add(Menu.NONE, R.id.menu_activity_main_search, 2, "Autocomplete");
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar menu
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // Configure the click on each item of the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                // Define the region
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(currentLocation.getLatitude() - 0.01, currentLocation.getLongitude() - 0.01),
                        new LatLng(currentLocation.getLatitude() + 0.01, currentLocation.getLongitude() + 0.01));

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setLocationBias(bounds)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .build(this);

                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ------------------------------------------------------------
    // CONFIGURATION Get back the result of the
    // placeAutocomplete and open the DetailRestaurantActivity
    // for the user's choice
    // ------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent intent = new Intent(this, RestaurantDetailActivity.class);
                intent.putExtra(PLACE_ID_RESTAURANT, place.getId());
                startActivity(intent);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation adapter item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mylunch) {
            startDetailActivity();


        } else if (id == R.id.nav_settings) {
            openProfileActivity();

        } else if (id == R.id.nav_chat) {
            openChatActivity();
        } else if (id == R.id.nav_search) {
            Intent intent = new Intent(this, NotificationAndSearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, SplashLunchActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // ----------------------------
    // CONFIGURATION NavigationView
    // ----------------------------
    private void configureNavigationView() {

        navigationView.setNavigationItemSelectedListener(this);

        if (this.getCurrentUser() != null) {
            // Customize image profile
            View headView = navigationView.getHeaderView(0);
            ImageView imgProfile = headView.findViewById(R.id.image_user);
            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .override(200, 200)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile);
                //Customize user data username and name
                String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ?
                        getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
                String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                        getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

                TextView textUsername = headView.findViewById(R.id.name_user);
                textUsername.setText(username);
                TextView textEmail = headView.findViewById(R.id.email_user);
                textEmail.setText(email);
            }
        }
    }

    // --------------------
    // BottomNavigationView
    // --------------------
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    getSupportActionBar().setTitle(R.string.toolbar_title);
                    nav_click = 1;
                    invalidateOptionsMenu();
                    return true;

                case R.id.bottom_navigation_list_restaurant:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    getSupportActionBar().setTitle(R.string.toolbar_title);
                    nav_click = 2;
                    invalidateOptionsMenu();
                    return true;

                case R.id.bottom_navigation_list_workmates:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    getSupportActionBar().setTitle(R.string.workmates);
                    nav_click = 3;
                    invalidateOptionsMenu();
                    return true;
            }
            return false;
        }
    };


    // --------------------
    // searchByRESTAURANT
    // --------------------

    private void searchNearbyRestaurants(double latitude, double longitude) {
        Log.d(TAG, "searchNearbyRestaurants: ");
        String keyword = "";
        String key = BuildConfig.API_KEY;
        //String lat = String.valueOf(currentLocation.getLatitude());
        //String lng = String.valueOf(currentLocation.getLongitude());
        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        String location = lat + "," + lng;

        Call<NearbyPlacesList> call;
        ApiInterface googleMapService = ApiClient.getClient().create(ApiInterface.class);
        call = googleMapService.getNearBy(location, radius, type, keyword, key);
        call.enqueue(new Callback<NearbyPlacesList>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlacesList> call, @NonNull Response<NearbyPlacesList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        results = response.body().getResults();
                        fragment1.updateNearbyPlaces(results);

                        fragment2.updateNearbyPlaces(results);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlacesList> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void startDetailActivity() {
        String userId=  UserRepository.getCurrentUserId();
        UserRepository.getUser(userId).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            String lunch;
            if (user != null) {
                lunch = user.getRestoToday();
                if (lunch.equals("")) {
                    Toast.makeText(mContext, R.string.no_lunch, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
                    intent.putExtra(PLACE_ID_RESTAURANT, lunch);
                    startActivity(intent);
                }
            }
        });
    }


    // ----------------------------
    // CONFIGURATION ProfileActivity
    // ----------------------------
    private void openProfileActivity() {
        Intent profileActivity = new Intent(LunchActivity.this, ProfileActivity.class);
        startActivity(profileActivity);
    }

    // ----------------------------
    // CONFIGURATION ChatActivity
    // ----------------------------
    private void openChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    // ----------------------------
    // CONFIGURATION permissions
    // ----------------------------
    private void getLocationPermission() {
        FusedLocationProviderClient mFusedLocationProviderClient;
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // onComplete: found location
                            currentLocation = (Location) task.getResult();
                            // We pass the user's position to the fragment map
                            assert currentLocation != null;

                            fragment1.setUserLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            fragment2.setUserLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            searchNearbyRestaurants(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }
                    }
                });

            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    // ----------------------------
    //     permission result
    // ----------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // onRequestPermissionsResult: called
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            // onRequestPermissionsResult: permissions failed
                            return;
                        } else {
                            // onRequestPermissionsResult: Permissions granted
                            mLocationPermissionGranted = true;
                            getLocationPermission();
                        }
                    }
                }
            }
        }
    }
}
