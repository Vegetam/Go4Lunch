package com.francescomalagrino.go4lunch.data;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.repo.RestaurantRepository;
import com.francescomalagrino.go4lunch.repo.UserRepository;
import com.francescomalagrino.go4lunch.view.LunchActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationsService extends FirebaseMessagingService {

    private final UserRepository userRepository = UserRepository.getInstance();
    private final RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();
    protected List<User> joiningUsers = new ArrayList<>();
    protected String address;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            // Get message sent by Firebase
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            getUserData();
            getRestaurantData();


            try {
                if (UserRepository.getMyUser().getReservation() != null  && UserRepository.getMyUser().isNotification()) {
                    sendVisualNotification(notification);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void getRestaurantData() {
        //joiningUsers = userRepository.getJoiningUsers(UserRepository.getMyUser().getReservation());
        //address = RestaurantRepository.getAddressRestaurant(UserRepository.getMyUser().getReservation());
    }

    private void getUserData() {
        userRepository.fetchUserData();
    }

    private void sendVisualNotification(RemoteMessage.Notification notification) {

        // Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, LunchActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo_menu)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody() + " at " + UserRepository.getMyUser().getReservation() + " located " + address + " with " + joiningUsers)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase Messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // Show notification
        int NOTIFICATION_ID = 7;
        String NOTIFICATION_TAG = "GO4LUNCH";
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

}