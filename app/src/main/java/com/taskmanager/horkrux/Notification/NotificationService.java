package com.taskmanager.horkrux.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taskmanager.horkrux.Activites.MainActivity;
import com.taskmanager.horkrux.R;

import java.util.Random;

public class NotificationService extends FirebaseMessagingService {
    private final String CHANNEL_ID = "ID";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(this, MainActivity.class);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "newChannel", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("MY");
        channel.enableLights(true);
        channel.setLightColor(Color.WHITE);
        manager.createNotificationChannel(channel);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent intent1 = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        Notification notification;

        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.mipmap.ic_launcher_round);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getData().get("notificationTitle"))
                .setContentText(remoteMessage.getData().get("notificationMessage"))
                .setLargeIcon(notificationLargeIconBitmap)
                .setSmallIcon(R.drawable.ic_baseline_explore_24)
                .setAutoCancel(true)
                .setContentIntent(intent1)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("notificationMessage")))
                .build();

//        notification = new Notification.Builder(this, CHANNEL_ID)
//                .setContentTitle(remoteMessage.getData().get("title"))
//                .setContentText(remoteMessage.getData().get("message"))
//                .setSmallIcon(R.drawable.ic_baseline_explore_24)
//                .setContentIntent(intent1)
//                .build();


        manager.notify(notificationId, notification);
    }
}
