package com.example.ahmeda.uberdriverapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

public class GCMListenerService extends GcmListenerService {
    public GCMListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(data.getString("to").equalsIgnoreCase("client")) {
            handleRideData(data.getString("from_lat"), data.getString("from_lng"), data.getString("to_lat"), data.getString("to_lat"));
        }
        else {

        }
    }

    private void handleRideData(String from_lat, String from_lng, String to_lat, String to_lng) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("Coming Ride");
        notificationBuilder.setContentText("A client request a ride");
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.cast_ic_notification_1);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        Intent driverLocationIntent = new Intent("Ride_data");
        driverLocationIntent.putExtra("from_lat", Double.valueOf(from_lat));
        driverLocationIntent.putExtra("from_lng", Double.valueOf(from_lng));
        driverLocationIntent.putExtra("to_lat", Double.valueOf(to_lat));
        driverLocationIntent.putExtra("to_lng", Double.valueOf(to_lng));
        sendBroadcast(driverLocationIntent);
    }


}
