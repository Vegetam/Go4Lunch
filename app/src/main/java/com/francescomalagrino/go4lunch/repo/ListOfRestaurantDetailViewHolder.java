package com.francescomalagrino.go4lunch.repo;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.francescomalagrino.go4lunch.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.francescomalagrino.go4lunch.R;


/**----------------------------------------------------------
 * ViewHolder for items in RestaurantDetailsActivity
 ------------------------------------------------------------*/
class ListOfRestaurantDetailViewHolder extends RecyclerView.ViewHolder{
    private TextView nameTextView ;
    private ImageView photo;
    private Context mContext;

    ListOfRestaurantDetailViewHolder(View itemView, Context context) {
        super(itemView);

        mContext = context;
        nameTextView = itemView.findViewById(R.id.workmates_TextView);
        photo = itemView.findViewById(R.id.workmates_ImageView);
    }

    void updateWithDetails(final String clientId, final RequestManager glide) {

        UserRepository.getUser(clientId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String text;
                if (user != null) {
                    text = user.getUsername() + mContext.getResources().getString(R.string.isjoining);
                    nameTextView.setText(text);
                    nameTextView.setTypeface(null, Typeface.NORMAL);
                    nameTextView.setTextColor(mContext.getResources().getColor(R.color.colorMyBlack));
                }

                // Images
                if (user != null) {
                    if (user.getUrlPicture() != null) {
                        if (user.getUrlPicture().length()>0){
                            glide.load(user.getUrlPicture())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(photo);
                        } else {
                            photo.setImageResource(R.drawable.ic_baseline_people_24);
                        }
                    }
                }
            }
        });
    }

}
