package com.francescomalagrino.go4lunch.repo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.francescomalagrino.go4lunch.data.details.RestaurantDetailResult;
import com.google.android.gms.maps.model.LatLng;
import com.francescomalagrino.go4lunch.R;


import java.util.List;

/**----------------------------------------------------
 * Adapter for list of restaurants in ListFragment
 ------------------------------------------------------*/
public class ListOfRestaurantsAdapter extends RecyclerView.Adapter<ListOfRestaurantsViewHolder> {

    private List<RestaurantDetailResult> restaurantList;
    private RequestManager glide;
    private OnItemClickedListener mListener;
    private int length;
    private LatLng latlng;

    public interface OnItemClickedListener{
        void OnItemClicked(int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    // Constructor
    public ListOfRestaurantsAdapter(List<RestaurantDetailResult> restList, RequestManager glide, int length, LatLng latLng) {
        this.restaurantList = restList;
        this.glide = glide;
        this.length =  length;
        this.latlng = latLng;
    }

    @NonNull
    @Override
    public ListOfRestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates adapter holder and inflates its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new ListOfRestaurantsViewHolder(view, mListener, latlng);
    }

    // update adapter holder
    @Override
    public void onBindViewHolder(@NonNull ListOfRestaurantsViewHolder viewHolder, int position) {
        viewHolder.updateWithDetailsRestaurants(this.restaurantList.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return length ;
    }
}
