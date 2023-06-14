package com.francescomalagrino.go4lunch.ui.details;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.databinding.*;
import com.francescomalagrino.go4lunch.ui.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    private DetailsViewModel mViewModel;
    private String photoURL;
    protected boolean liked = false;

    protected boolean hasReserved = false;
    protected List<User> users;
    Restaurant restaurant = new Restaurant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restaurant = (Restaurant) getIntent().getSerializableExtra("Restaurant");

        configureViewModel();

        setupListener();
        setupRecyclerView();
        updateView();
    }

    private void updateView() {

        binding.tvName.setText(restaurant.getRestoName());
        binding.tvAddress.setText(restaurant.getFormattedAddress());

        if (liked){
            binding.starToLike.setImageTintList(AppCompatResources.getColorStateList(this, R.color.colorSand));
        }
        Log.e("reserved" , hasReserved + "");
        if (hasReserved){
            binding.btnReserved.setImageTintList(AppCompatResources.getColorStateList(this, R.color.colorMyGreen));
        }



        Glide.with(this)
                .load(restaurant.getPhotoUrl())
                .error(R.drawable.img_drawers)
                .centerCrop()
                .into(binding.imgPhotoDetail);



        // SETUP STARS FOR RATING
        double rate = restaurant.getRating();
        Gson gson = new Gson();
        Log.e("Restaurant Object" , gson.toJson(restaurant));
        if (rate > 0.5){

            binding.imgRateFirst.setVisibility(View.VISIBLE);
            if (rate > 1.5){

                binding.imgRateSecond.setVisibility(View.VISIBLE);
            }
            if (rate > 2.5){
                binding.imgRateThird.setVisibility(View.VISIBLE);
            }
        }else{
            binding.imgRateFirst.setVisibility(View.INVISIBLE);
            binding.imgRateSecond.setVisibility(View.INVISIBLE);
            binding.imgRateThird.setVisibility(View.INVISIBLE);
        }
        Log.e(TAG, "configureViewModel: "+restaurant.getPlaceId() );

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        Gson gson = new Gson();
        Log.e("HasReseverBY" , gson.toJson(restaurant.getHasBeenReservedBy()));
        if(restaurant.getHasBeenReservedBy() !=null) {
            RecyclerView recyclerView = findViewById(R.id.recyclerview_detail);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            DetailsAdapter detailsAdapter = new DetailsAdapter(this, restaurant.getHasBeenReservedBy());
            recyclerView.setAdapter(detailsAdapter);
            detailsAdapter.notifyDataSetChanged();
        }
    }

    private void configureViewModel() {

        this.mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(DetailsViewModel.class);
        this.mViewModel.init(restaurant.getPlaceId(),restaurant.getRestoName());
        liked = mViewModel.getFavoriteRestaurant(restaurant.getRestoName());
        hasReserved = mViewModel.getReservation(restaurant);
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(binding.viewDetail, message, Snackbar.LENGTH_SHORT).show();
    }

    private void setupListener() {

        // CALL BUTTON LISTENER
        binding.btnCall.setOnClickListener(v -> {
            if(restaurant.getPhoneNumber() !=null){

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + restaurant.getPhoneNumber()));
                startActivity(intent);
            }else{
                showSnackBar(getString(R.string.no_phone));
            }
        });

        // LIKE BUTTON LISTENER
        binding.btnLike.setOnClickListener(v -> {
            if(liked){
                liked = false;
                binding.starToLike.setImageTintList(AppCompatResources.getColorStateList(this, R.color.colorMyGrey));
                mViewModel.removeRestaurantLiked(restaurant.getRestoName());
            }else{
                liked = true;
                binding.starToLike.setImageTintList(AppCompatResources.getColorStateList(this, R.color.colorSand));
                mViewModel.addRestaurantLiked(restaurant.getRestoName());
            }
        });

        // WEBSITE BUTTON LISTENER
        binding.btnWebsite.setOnClickListener(v -> {
            if(mViewModel.getDetailRestaurant().getWebsite() !=null){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mViewModel.getDetailRestaurant().getWebsite()));
                startActivity(browserIntent);
            }else{
                showSnackBar(getString(R.string.no_website));
            }
        });

        // RESERVATION BUTTON LISTENER
        binding.btnReserved.setOnClickListener(v -> {
            if(hasReserved){
                hasReserved = false;
                binding.btnReserved.setImageTintList(AppCompatResources.getColorStateList(this, R.color.colorMyGrey));
                mViewModel.removeReservation(restaurant);
            }else{
                  mViewModel.addReservation(restaurant).observe(this, isSuccessfull -> {
                      if(isSuccessfull) {
                          hasReserved = true;
                          binding.btnReserved.setImageTintList(AppCompatResources.getColorStateList(this, R.color.colorGreen));
                          this.setupRecyclerView();
                      }else {
                          Log.e("Error", "Error during adding");
                      }
                  });
            }
            this.setupRecyclerView();
        });
    }
}