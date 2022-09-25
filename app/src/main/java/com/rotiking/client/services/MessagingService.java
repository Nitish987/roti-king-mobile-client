package com.rotiking.client.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rotiking.client.R;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.utils.Promise;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null) {
            pushNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        if (Auth.isUserAuthenticated(this)) {
            Auth.Login.updateMessageToken(this, token, new Promise<String>() {
                @Override
                public void resolving(int progress, String msg) {}

                @Override
                public void resolved(String o) {
                }

                @Override
                public void reject(String err) {
                }
            });
        }
    }

    private void pushNotification(String title, String body) {
        final String CHANNEL_ID = "RotiKing " + new Random().nextInt(100);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);

        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), Notification.AUDIO_ATTRIBUTES_DEFAULT);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID).setContentText(title).setContentTitle(body).setSmallIcon(R.drawable.logo);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(100), notification.build());
    }
}
