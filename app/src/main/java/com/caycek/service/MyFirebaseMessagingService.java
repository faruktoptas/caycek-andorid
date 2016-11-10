package com.caycek.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.caycek.MainActivity;
import com.caycek.R;
import com.caycek.util.CaycekUtils;
import com.caycek.util.PreferenceWrapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by faruktoptas on 22/09/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            showSimpleNotification(remoteMessage.getData().get("message"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            showSimpleNotification(remoteMessage.getNotification().getBody());
            String pushMessage = remoteMessage.getData().get("message");
            String sender = pushMessage.substring(0, pushMessage.indexOf(":"));
            PreferenceWrapper.getInstance().writeLastPush(sender + " " + CaycekUtils.getTimeStamp());
            Intent intent = new Intent();
            intent.setAction("refresh");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    private void showSimpleNotification(String body) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.notif_icon)
                        .setContentText(body);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
    }
}
