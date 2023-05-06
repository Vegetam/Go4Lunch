package com.francescomalagrino.go4lunch.ui.message;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.databinding.ActivityChatBinding;
import com.francescomalagrino.go4lunch.repo.ChatMessage;
import com.francescomalagrino.go4lunch.ui.ViewModelFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel mViewModel;
    private ActivityChatBinding binding;
    private User receivedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        receivedUser = (User) getIntent().getSerializableExtra("User");
        configureViewModel();
        loadReceiverDetails();
        init();
        setupListener();
    }

    private void setupListener() {
        binding.btnSent.setOnClickListener(v -> {
            HashMap<String, Object> message = new HashMap<>();
            message.put("SENDER_ID", mViewModel.getCurrentUserUID());
            message.put("RECEIVED_ID", receivedUser.getUid());
            message.put("INPUT_MESSAGE", Objects.requireNonNull(binding.inputMessage.getText()).toString());
            message.put("TIMESTAMP", new Date());
            mViewModel.sentMessage(message);
            binding.inputMessage.setText("");
        });
        binding.btnCheck.setOnClickListener(v -> Log.e(TAG, "btnCheck: "+ Objects.requireNonNull(mViewModel.getChatMessages().getValue()).size() ));
        binding.backArrow.setOnClickListener(v-> onBackPressed());
    }

    private void init() {
        mViewModel.listenMessage(receivedUser.getUid());
        mViewModel.getChatMessages().observe(this, this::updateView);
    }


    private void updateView(List<ChatMessage> chatMessages) {
        if (chatMessages != null){

            ChatAdapter chatAdapter = new ChatAdapter(this, chatMessages, mViewModel.getCurrentUserUID(), receivedUser);
                binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.chatRecyclerView.setHasFixedSize(true);
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.setAdapter(chatAdapter);
                binding.chatRecyclerView.scrollToPosition(chatMessages.size()-1);

        }

    }

    private void loadReceiverDetails() {
        binding.tvInterlocutorName.setText(getIntent().getStringExtra("Name"));
    }

    private void configureViewModel() {
        this.mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ChatViewModel.class);
    }

}