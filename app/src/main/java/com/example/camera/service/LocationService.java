package com.example.camera.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.camera.constants.LocationConstants;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {
    public static String SERVICE_MESSAGE = "Send Data From service";
    Context context;
    double speed;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLocations() != null) {
                double speed = locationResult.getLastLocation().getSpeed();
                setSpeed(speed);
                String latitude = String.valueOf(locationResult.getLastLocation().getLatitude());
                String longitude = String.valueOf(locationResult.getLastLocation().getLongitude());
                sendMessageToUi("Latitude :"+latitude+" Longitude: "+longitude);
                Log.d("Location Service Lat", "onLocationResult: " +latitude + " " + longitude);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "LOCATION_NOTIFICATION_CHANNEL";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setContentTitle("Tracking your route");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Tracking");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null &&
                    notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        notificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel uses location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(4000)                                     //means - set the interval in which you want to get locations
                .setFastestInterval(2000)                             //means - if a location is available sooner you can get it (i.e. another app is using the location services).
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(LocationConstants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(LocationConstants.ACTION_START_SERVICE)) {
                    startLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        stopLocationService();
        super.onDestroy();
    }

    private void sendMessageToUi(String location) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra("Location", location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}