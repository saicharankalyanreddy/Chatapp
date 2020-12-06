package com.example.firebaseappdemo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firebaseappdemo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty()){
            shownotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
        else {
            shownotification(remoteMessage.getData());
        }
    }

    void shownotification(Map<String,String> data){
        String title = data.get("title").toString();
        String body = data.get("body").toString();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.firebaseappdemo.services.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Hello brother");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        notificationbuilder.setAutoCancel(true);
        notificationbuilder.setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_os_notification_fallback_white_24dp);
        notificationbuilder.setContentTitle(title).setContentText(body).setContentInfo("info");

        notificationManager.notify(new Random().nextInt(),notificationbuilder.build());
    }

    private void shownotification(String title,String body){

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.firebaseappdemo.services.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Hello brother");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        notificationbuilder.setAutoCancel(true);
        notificationbuilder.setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_os_notification_fallback_white_24dp);
        notificationbuilder.setContentTitle(title).setContentText(body).setContentInfo("info");

        notificationManager.notify(new Random().nextInt(),notificationbuilder.build());


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("TOKENFIREBASE",s);
    }
}
