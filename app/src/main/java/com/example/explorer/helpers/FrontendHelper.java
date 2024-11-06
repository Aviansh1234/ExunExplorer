package com.example.explorer.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;

public class FrontendHelper {
    public static void setLocationListener(LocationManager locationManager, Context context) {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println("we are here");
                GeoPoint curr = new GeoPoint(location.getLatitude(), location.getLongitude());
                try {
                    BackendHelper.sendLocation(curr);
//                    Toast.makeText(context, "AHHHHHHHHHHHHH", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        LocationListener locationListener2 = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println("we are here2");
                GeoPoint curr = new GeoPoint(location.getLatitude(), location.getLongitude());
                try {
                    BackendHelper.sendLocation(curr);
//                    Toast.makeText(context, "AHHHHHHHHHHHHH", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        System.out.println("entered listener");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        System.out.println("setting listener");
        Toast.makeText(context, "AHHHHHHHHHHHHH", Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
        if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null) {
            try {
                BackendHelper.sendLocation(new GeoPoint(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(), locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
