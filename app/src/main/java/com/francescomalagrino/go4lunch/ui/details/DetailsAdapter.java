package com.francescomalagrino.go4lunch.ui.details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.User;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailViewHolder>{

    final Context context;
    final List<User> users;

    public DetailsAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }



    @NonNull
    @Override
    public DetailsAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_people,parent,false);
        return new DetailViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailsAdapter.DetailViewHolder holder, int position) {

        User user = users.get(position);

        Glide.with(context)
                .load(user.getUrlPicture())
                .error(R.drawable.ic_anon_user_48dp)
                .circleCrop()
                .into(holder.img_photo);

        holder.tv_status.setText(user.getUsername()+" "+context.getString(R.string.joining));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {

        final ImageView img_photo;
        final TextView tv_status;
        final ConstraintLayout item;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_workmates);
            img_photo = itemView.findViewById(R.id.img_photo);
            tv_status = itemView.findViewById(R.id.tv_status);
        }
    }
}
