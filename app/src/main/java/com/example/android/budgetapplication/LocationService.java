package com.example.android.budgetapplication;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;

    public Location location;


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        //Logging.i(CLAZZ, "onHandleIntent", "invoked");
        Log.e("abc", "in LocationService");
        if (intent.getAction().equals("startListening")) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        return START_STICKY;

    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public void onLocationChanged(final Location location) {
        this.location = location;
        // TODO this is where you'd do something like context.sendBroadcast()
        Intent intent = new Intent();
        intent.setAction("com.example.android.budgetapplication/locationResult");
        intent.putExtra("latitude", String.valueOf(location.getLatitude()));
        intent.putExtra("longitude", String.valueOf(location.getLongitude()));
        sendBroadcast(intent);
        //Stop getting updates when done
        locationManager.removeUpdates(this);
        locationManager = null;
    }

    public void onProviderDisabled(final String provider) {
        //Check if GPS is disabled, open settings

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
           onProviderDisabled("gps");

        }else{
            return;
        }
    }


    public void onProviderEnabled(final String provider) {
    }

    public void onStatusChanged(final String arg0, final int arg1, final Bundle arg2) {
    }

}
