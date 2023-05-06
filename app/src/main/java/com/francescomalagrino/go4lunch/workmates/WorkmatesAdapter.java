package com.francescomalagrino.go4lunch.workmates;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.francescomalagrino.go4lunch.fragment.ChatActivity;


import java.io.Serializable;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    final Context context;
    final List<User> users;

    public WorkmatesAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_people, parent, false);
        return new WorkmatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.WorkmatesViewHolder holder, int position) {

        User user = users.get(position);

        holder.item.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("User", (Serializable) user);
            intent.putExtra("Name", user.getUsername());
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(user.getUrlPicture())
                .error(R.drawable.ic_anon_user_48dp)
                .circleCrop()
                .into(holder.img_photo);


        if (user.getReservation() == null) {
            holder.tv_status.setText(String.format("%s hasn't decided yet", user.getUsername()));
            holder.tv_status.setTextColor(Color.GRAY);
        } else {
            holder.tv_status.setText(String.format("%s is eating at %s", user.getUsername(), user.getReservation()));
            holder.tv_status.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        final ImageView img_photo;
        final TextView tv_status;
        final ConstraintLayout item;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_workmates);
            img_photo = itemView.findViewById(R.id.img_photo);
            tv_status = itemView.findViewById(R.id.tv_status);
        }
    }

}