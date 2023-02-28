package com.francescomalagrino.go4lunch.repo;

import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.francescomalagrino.go4lunch.BuildConfig;
import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.details.Period;
import com.francescomalagrino.go4lunch.data.details.RestaurantDetailResult;
import com.francescomalagrino.go4lunch.utils.LunchDateFormat;
import com.francescomalagrino.go4lunch.utils.RestaurantRate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**-----------------------------------------------------------------
 * ViewHolder for items of list of restaurants in ListFragment
 -------------------------------------------------------------------*/
public class ListOfRestaurantsViewHolder extends RecyclerView.ViewHolder{

    private TextView nameTextView, addressTextView, openTextView, proximityTextView, loversTextView;
    private ImageView star1, star2, star3, photo;
    private LatLng myLatLng;
    private boolean textOK = false;
    private String today;



    public ListOfRestaurantsViewHolder(View itemView, final ListOfRestaurantsAdapter.OnItemClickedListener listener, LatLng latLng) {
        super(itemView);

        myLatLng = latLng;

        nameTextView =  itemView.findViewById(R.id.restaurant_name);
        addressTextView =  itemView.findViewById(R.id.restaurant_address);
        openTextView =  itemView.findViewById(R.id.restaurant_openinghours);
        proximityTextView =  itemView.findViewById(R.id.restaurant_proximity);
        loversTextView =  itemView.findViewById(R.id.restaurant_lovers_nb);

        star1 =  itemView.findViewById(R.id.restaurant_star1);
        star2 =  itemView.findViewById(R.id.restaurant_star2);
        star3 =  itemView.findViewById(R.id.restaurant_star3);
        photo =  itemView.findViewById(R.id.restaurant_photo);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null) {
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION) {
                        listener.OnItemClicked(position);
                    }
                }
            }
        });
    }


    void updateWithDetailsRestaurants(RestaurantDetailResult restaurantDetail, RequestManager glide) {
        // Name of restaurant
        this.nameTextView.setText(restaurantDetail.getName());

        //Address
        String address_short = restaurantDetail.getAddressComponents().get(0).getShortName() + ", " + restaurantDetail.getAddressComponents().get(1).getShortName();
        this.addressTextView.setText(address_short);

        // Opening hours
        openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGrey));
        if(restaurantDetail.getOpeninghours()!= null) {
            // default value that will be overwritten with today's schedules if the restaurant is open today
            isRestaurantOpen(restaurantDetail);
            textOK = false;
        } else {
            openTextView.setText(R.string.no_hours);
        }

        //Distance
        float distance;
        float results[] = new float[10];
        double restaurantLat = restaurantDetail.getGeometry().getLocation().getLat();
        double restaurantLng = restaurantDetail.getGeometry().getLocation().getLng();
        double myLatitude = myLatLng.latitude;
        double myLongitude = myLatLng.longitude;
        Location.distanceBetween(myLatitude, myLongitude, restaurantLat, restaurantLng,results);
        distance = results[0];
        String dist =  Math.round(distance)+"m";
        proximityTextView.setText(dist);

        // Assign the number of stars
        if (restaurantDetail.getRating()!= 0.0F) {
            float rate = restaurantDetail.getRating();
            RestaurantRate myRate = new RestaurantRate(rate, star1, star2, star3);
        } else {
            RestaurantRate myRate = new RestaurantRate(0, star1, star2, star3);
        }

        // Images
        if (restaurantDetail.getPhotos() != null && !restaurantDetail.getPhotos().isEmpty()){
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurantDetail.getPhotos().get(0).getPhotoReference()+"&key="+ BuildConfig.API_KEY).into(photo);
        } else {
            this.photo.setImageResource(R.drawable.ic_menu_camera);
        }

        // Number of interested colleagues
        // Set to 0 by default
        loversTextView.setText("0");
        LunchDateFormat forToday = new LunchDateFormat();
        today = forToday.getTodayDate();

        RestaurantHelper.getRestaurant(restaurantDetail.getPlaceId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                    // Date check
                    Date dateRestaurantSheet;
                    if (restaurant != null) {
                        dateRestaurantSheet = restaurant.getDateCreated();
                        LunchDateFormat myDate = new LunchDateFormat();
                        String dateRegistered = myDate.getRegisteredDate(dateRestaurantSheet);
                        if (dateRegistered.equals(today)) {
                            // Number of interested colleagues
                            List<String> listUsers = restaurant.getClientsTodayList();
                            String text = String.valueOf(listUsers.size());
                            loversTextView.setText(text);
                        }
                    }
                }
            }
        });
    }

    private void isRestaurantOpen(RestaurantDetailResult restaurantDetail) {
        Calendar calendar = Calendar.getInstance();
        openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGrey));
        openTextView.setText(openTextView.getResources().getString(R.string.closed_today));

        for(Period period: restaurantDetail.getOpeninghours().getPeriods()){
            if(period.getClose() == null) {
                openTextView.setText(openTextView.getResources().getString(R.string.always_open));
            } else {
                String text;
                String textTime;
                if(period.getClose().getDay() == calendar.get(Calendar.DAY_OF_WEEK)-1&&!textOK) {
                    //textOK allows you to manage cases where there are several opening hours for the same day
                    LunchDateFormat hour = new LunchDateFormat();
                    switch (getOpeningHour(period)) {
                        case 1:
                            openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorPrimary));
                            text = openTextView.getResources().getString(R.string.open_at);
                            textTime = hour.getHoursFormat(period.getOpen().getTime());
                            text+=textTime;
                            openTextView.setText(text);

                            break;
                        case 2:
                            openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGreen));
                            text = openTextView.getResources().getString(R.string.open_until);

                            textTime = hour.getHoursFormat(period.getClose().getTime());
                            text+=textTime;
                            openTextView.setText(text);

                            break;
                        case 3:
                            openTextView.setTextColor(openTextView.getResources().getColor(R.color.colorMyGrey));
                            openTextView.setText(openTextView.getResources().getString(R.string.closed));
                    }
                }
            }
        }
    }

    // Method that get opening hours from GooglePlaces
    private int getOpeningHour(Period period){

        Calendar calendar = Calendar.getInstance();
        int currentHour = 1200;
        if (calendar.get(Calendar.MINUTE)<10) {
            currentHour = Integer.parseInt("" + calendar.get(Calendar.HOUR_OF_DAY) + "0" +calendar.get(Calendar.MINUTE));
        } else {
            currentHour = Integer.parseInt("" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE));
        }
        int closureHour = Integer.parseInt(period.getClose().getTime());
        int openHour = Integer.parseInt(period.getOpen().getTime());


        if (currentHour<openHour) {
            textOK = true; // We are earlier than the first schedule so do not go compare with the second
            return 1;
        }
        else if (currentHour>openHour&&currentHour<closureHour) {
            textOK = true; // We are in the first time slot so do not go compare with the second
            return 2;
        }
        else return 3;
    }
}
