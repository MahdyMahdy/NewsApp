package com.example.newsapp;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class MyFirebaseMessagingServices extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getFirebaseMessage(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),remoteMessage.getNotification().getBody());
    }

    public void getFirebaseMessage(String title,String msg)
    {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"channel")
                .setContentTitle(title).setContentText(msg).setAutoCancel(true);
        NotificationManagerCompat manager=NotificationManagerCompat.from(this);
        manager.notify(101,builder.build());
    }
}
