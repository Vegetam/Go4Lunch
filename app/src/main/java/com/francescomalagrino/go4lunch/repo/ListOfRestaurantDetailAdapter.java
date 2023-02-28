package com.francescomalagrino.go4lunch.repo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.francescomalagrino.go4lunch.R;


import java.util.List;

/**----------------------------------------------------
 * Adapter for list in RestaurantDetailsActivity
 ----------------------------------------------------*/
public class ListOfRestaurantDetailAdapter extends RecyclerView.Adapter<ListOfRestaurantDetailViewHolder> {

    private List<String> clientsList;
    private RequestManager glide;
    private int length;

    // Constructor
    public ListOfRestaurantDetailAdapter(List<String> clientsList, RequestManager glide, int length) {
        this.clientsList = clientsList;
        this.glide = glide;
        this.length = length;
    }

    @NonNull
    @Override
    public ListOfRestaurantDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates adapter holder and inflates its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates, parent, false);
        return new ListOfRestaurantDetailViewHolder(view, context);
    }

    // update adapter holder
    @Override
    public void onBindViewHolder(@NonNull ListOfRestaurantDetailViewHolder viewHolder, int position) {
        viewHolder.updateWithDetails(this.clientsList.get(position), this.glide);
    }

    // return the total count of items in the list
    @Override
    public int getItemCount() {
        return length;
    }

}