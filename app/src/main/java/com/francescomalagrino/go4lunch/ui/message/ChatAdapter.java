package com.francescomalagrino.go4lunch.ui.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.databinding.ItemReceivedMessageBinding;
import com.francescomalagrino.go4lunch.databinding.ItemSentMessageBinding;
import com.francescomalagrino.go4lunch.repo.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final User user;
    private final List<ChatMessage> chatMessage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(Context context, List<ChatMessage> chatMessage, String senderId, User user) {
        this.context = context;
        this.senderId = senderId;
        this.chatMessage = chatMessage;
        this.user = user;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceivedMessageViewHolder(
                    ItemReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessage.get(position));
        }else{
            ((ReceivedMessageViewHolder) holder).setData(chatMessage.get(position), context, user);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessage.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else{
            return  VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemSentMessageBinding binding;

        public SentMessageViewHolder(ItemSentMessageBinding itemSentMessageBinding) {
            super(itemSentMessageBinding.getRoot());
            binding = itemSentMessageBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.tvMessage.setText(chatMessage.message);
            binding.tvDataTime.setText(chatMessage.dataTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemReceivedMessageBinding binding;

        public ReceivedMessageViewHolder(ItemReceivedMessageBinding itemReceivedMessageBinding) {
            super(itemReceivedMessageBinding.getRoot());
            binding = itemReceivedMessageBinding;

        }

        void setData(ChatMessage chatMessage, Context context, User user){
            binding.tvChat.setText(chatMessage.message);
            binding.tvDataTime.setText(chatMessage.dataTime);

            Glide.with(context)
                    .load(user.getUrlPicture())
                    .error(R.drawable.ic_anon_user_48dp)
                    .circleCrop()
                    .into(binding.imgProfile);
        }
    }
}
