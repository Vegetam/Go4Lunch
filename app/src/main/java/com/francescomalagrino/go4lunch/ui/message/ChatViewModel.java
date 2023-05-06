package com.francescomalagrino.go4lunch.ui.message;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.francescomalagrino.go4lunch.repo.ChatMessage;
import com.francescomalagrino.go4lunch.repo.ChatRepository;
import com.francescomalagrino.go4lunch.repo.UserRepository;

import java.util.HashMap;
import java.util.List;

public class ChatViewModel extends ViewModel {
    // REPOSITORIES

    private final UserRepository userSource;

    private final ChatRepository chatSource;

    // DATA


    // CONSTRUCTOR

    public ChatViewModel(UserRepository userSource, ChatRepository chatSource) {
        this.userSource = userSource;
        this.chatSource = chatSource;
    }


    public String getCurrentUserUID(){return userSource.getCurrentUserUID();}

    public void sentMessage(HashMap<String, Object> message) {
        chatSource.sentMessage(message);
    }

    public void listenMessage(String receivedId){
        chatSource.listenMessage(receivedId);
    }

    public MutableLiveData<List<ChatMessage>> getChatMessages() {
        return chatSource.getChatMessages();
    }
}
