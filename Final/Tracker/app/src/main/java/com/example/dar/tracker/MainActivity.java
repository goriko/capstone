package com.example.dar.tracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView textViewLocation;
    private static final int REQUEST_PERMISSION_FINE_LOCATION_RESULT = 0;
    private LocationManager locationManager;
    private Location user, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        textViewLocation = (TextView) findViewById(R.id.textViewLocation);

        destination = new Location("");
        destination.setLatitude(10.3540762);
        destination.setLongitude(123.91157580000001);

        final Handler handler = new Handler();
        final int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION )==
                            PackageManager.PERMISSION_GRANTED){
                        getLocation();
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                            Toast.makeText(getApplicationContext(), "Application required to access location", Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE_LOCATION_RESULT);
                    }
                }else{
                    getLocation();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_FINE_LOCATION_RESULT){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Application will not run without location permission", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void getLocation(){
        try{
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        textViewLocation.setText("Lat: "+location.getLatitude()+"\nLng: "+location.getLongitude());
        check(location);
    }

    public void check(Location location){
        user = location;
        if(user.distanceTo(destination) <= 1000){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notify_001")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("HAPIT NA ABOT")
                    .setContentText("Hapit naka")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, mBuilder.build());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
}
