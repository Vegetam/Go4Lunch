package com.francescomalagrino.go4lunch.repo;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public final class ChatRepository {

    private static final String CHAT_COLLECTION = "chats";
    private static volatile ChatRepository instance;
    private static final UserRepository userRepository = UserRepository.getInstance();
    private static List<ChatMessage> chatMessageList ;
    private final MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<List<ChatMessage>>() {};


    public ChatRepository() { }

    public static ChatRepository getInstance() {
        ChatRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatRepository.class) {
            if (instance == null) {
                instance = new ChatRepository();
            }
            return instance;
        }
    }

    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    public void sentMessage(HashMap<String, Object> message) {
        this.getChatCollection().add(message);
    }

    public void listenMessage(String receivedId){
        chatMessageList = new ArrayList<>();
        this.getChatCollection()
                .whereEqualTo("SENDER_ID",userRepository.getCurrentUserUID())
                .whereEqualTo("RECEIVED_ID",receivedId)
                .addSnapshotListener(eventListener);
        this.getChatCollection()
                .whereEqualTo("SENDER_ID",receivedId)
                .whereEqualTo("RECEIVED_ID",userRepository.getCurrentUserUID())
                .addSnapshotListener(eventListener);
        Log.e(TAG, "listenMessage: " );
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        }
        if (value != null){

            for(DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("SENDER_ID");
                    chatMessage.receivedId = documentChange.getDocument().getString("RECEIVED_ID");
                    chatMessage.message = documentChange.getDocument().getString("INPUT_MESSAGE");
                    chatMessage.dataTime = getReadableTime(documentChange.getDocument().getDate("TIMESTAMP"));
                    chatMessage.dateObject = documentChange.getDocument().getDate("TIMESTAMP");

                    if(chatMessageList.isEmpty()) {
                        chatMessageList.add(chatMessage);
                        chatMessages.setValue(chatMessageList);
                    }if(chatMessageList.contains(chatMessage)){
                        continue;
                    }else{
                        chatMessageList.add(chatMessage);
                        chatMessages.setValue(chatMessageList);
                    }
                }
                Collections.sort(Objects.requireNonNull(chatMessages.getValue()), (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            }
        }
    };

    public String getReadableTime(Date date){
        return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    public MutableLiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }
}