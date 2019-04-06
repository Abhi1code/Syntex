package com.matrixfrats.syntex;

import android.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by DELL on 04-02-2018.
 */

public class Uploadlatlongservice extends Service {

    private DatabaseReference databasereference;
    private android.location.LocationListener listener;
    private LocationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        databasereference = FirebaseDatabase.getInstance().getReference();
        listener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent broadcastlocation = new Intent("location update");
                broadcastlocation.putExtra("cordinates", location.getLatitude() + "," + location.getLongitude());
                sendBroadcast(broadcastlocation);
                updatedatabase(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(Uploadlatlongservice.this, "Friend Locator wants to access your location, please switch on GPS", Toast.LENGTH_LONG).show();
                Intent settingintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                settingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingintent);
            }
        };
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        checkforuser();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            manager.removeUpdates(listener);
        }
    }

    private String getsharedprefname() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", "N/A");
    }

    private void updatedatabase(Double lat, Double lon) {
        DatabaseReference reference = databasereference.child(getsharedprefname());
        reference.child("latlon").setValue(lat + "," + lon);

    }

    private void checkforuser() {

        databasereference.child(getsharedprefname()).child("accessed by").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (manager != null) {
                    manager.removeUpdates(listener);
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //String uid = snapshot.getKey();
                    Integer status = snapshot.getValue(Integer.class);
                    if(status.equals(1)){
                        if (ContextCompat.checkSelfPermission(Uploadlatlongservice.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Uploadlatlongservice.this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);

                        }
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (manager != null) {
                    manager.removeUpdates(listener);
                }
                //Intent errorintent = new Intent("error response");
                //sendBroadcast(errorintent);
            }
        });

    }
}
