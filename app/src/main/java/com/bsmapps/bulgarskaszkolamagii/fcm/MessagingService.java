package com.bsmapps.bulgarskaszkolamagii.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.bsmapps.bulgarskaszkolamagii.R;
import com.bsmapps.bulgarskaszkolamagii.home.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mlody Danon on 7/20/2017.
 */

public class MessagingService extends FirebaseMessagingService {

    private static String TAG = "MessagingService";
    private String mBody;
    private String mTitle;
    private String mPhotoUrl;
    private Bitmap largeIcon;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if( remoteMessage.getNotification() != null ){
            mBody = remoteMessage.getNotification().getBody();
            mTitle = remoteMessage.getNotification().getTitle();
            mPhotoUrl = remoteMessage.getNotification().getIcon();

            Log.d(TAG, "New Notification: "+mTitle+" | "+mBody);

            URL url;
            try {
                url = new URL(mPhotoUrl);
                largeIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            displayNotification();
        }
    }

    private void displayNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notificationBuilder
                = ( Notification.Builder ) new Notification.Builder(this)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.judge_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(mTitle)
                .setContentText(mBody)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setVibrate( new long[] { 1000, 1000 } );

        NotificationManager notificationManager
                = ( NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}