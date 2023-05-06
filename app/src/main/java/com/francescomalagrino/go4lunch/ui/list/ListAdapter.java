package com.francescomalagrino.go4lunch.ui.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.ui.details.DetailsActivity;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {


    protected final Context context;
    protected final List<Restaurant> restaurants;
    protected final Location location = new Location("location");
    protected final Location destination = new Location("destination");
    protected String photoURL;

    public ListAdapter(Context context, List<Restaurant> restaurants, Location location ) {
        this.context = context;
        this.restaurants = restaurants;

        location.setLatitude(location.getLatitude());
        location.setLongitude(location.getLongitude());
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return new ListViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        Restaurant restaurant = Objects.requireNonNull(restaurants).get(position);
        holder.item.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("Restaurant", restaurant);
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(restaurant.getPhotoUrl())
                .centerCrop()
                .error(R.drawable.img_drawers)
                .into(holder.img_photo);


        if(restaurant.getHasBeenReservedBy().size() > 0 ){
            holder.img_person.setVisibility(View.VISIBLE);
            holder.tv_joining.setVisibility(View.VISIBLE);
            holder.tv_joining.setText("("+restaurant.getHasBeenReservedBy().size()+")");
        }else{
            holder.img_person.setVisibility(View.INVISIBLE);
            holder.tv_joining.setVisibility(View.INVISIBLE);
        }


        holder.tv_name.setText(restaurant.getRestoName());
        Gson gson = new Gson();
        Log.e("RESTAURANT" , gson.toJson(restaurant));
        //holder.tv_type.setVisibility(View.GONE);
        holder.tv_address.setText(restaurant.getAddress());


        destination.setLatitude(restaurant.getLat());
        destination.setLongitude(restaurant.getLng());
        String metre = String.valueOf((int)location.distanceTo(destination)/1000);
        holder.tv_metre.setText(metre + " m");

        // SETTING OPENING
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().getOpenNow()) {
                holder.tv_opening.setText(R.string.Open_now);
            } else {
                holder.tv_opening.setText(R.string.Close);
            }
        }
        // SETTING STARS FOR RATING
        if (restaurant.getRating() > 0.5){
            holder.img_rate_first.setVisibility(View.VISIBLE);
            if (restaurant.getRating() > 1.5){
                holder.img_rate_second.setVisibility(View.VISIBLE);
            }
                if (restaurant.getRating() > 2.5){
                    holder.img_rate_third.setVisibility(View.VISIBLE);
                }
        }else{
            holder.img_rate_first.setVisibility(View.INVISIBLE);
            holder.img_rate_second.setVisibility(View.INVISIBLE);
            holder.img_rate_third.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(restaurants.size());
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{

        final ImageView img_photo;
        final ImageView img_person;
        final ImageView img_rate_first;
        final ImageView img_rate_second;
        final ImageView img_rate_third;
        final TextView tv_name;
        final TextView tv_metre;
        //final TextView tv_type;
        final TextView tv_address;
        final TextView tv_joining;
        final TextView tv_opening;
        final ConstraintLayout item;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_list);
            img_photo = itemView.findViewById(R.id.img_photo);
            img_person = itemView.findViewById(R.id.img_person);
            img_rate_first = itemView.findViewById(R.id.img_rate_first);
            img_rate_second = itemView.findViewById(R.id.img_rate_second);
            img_rate_third = itemView.findViewById(R.id.img_rate_third);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_metre = itemView.findViewById(R.id.tv_metre);
            //tv_type = itemView.findViewById(R.id.tv_type);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_joining = itemView.findViewById(R.id.tv_joining);
            tv_opening = itemView.findViewById(R.id.tv_opening);
        }
    }
}
