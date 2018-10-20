package com.example.dar.share;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private GeoDataClient geoDataClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private static final LatLngBounds latLngBounds = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private AutoCompleteTextView textVieworigin, textViewdestination;
    private Button buttonFind, buttonCreate, buttonTime;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private TextView textView;

    private LatLng origin;
    private LatLng destination;
    private String originString;
    private String destinationString;
    private Integer fareFrom;
    private Integer fareTo;
    private Integer estimatedTravelTime;
    private Integer departureHour;
    private Integer departureMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        textVieworigin = (AutoCompleteTextView) findViewById(R.id.editTextOrigin);
        textViewdestination = (AutoCompleteTextView) findViewById(R.id.editTextDestination);
        buttonFind = (Button) findViewById(R.id.buttonFind);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonTime = (Button) findViewById(R.id.buttonTime);
        textView = (TextView) findViewById(R.id.textView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init(){
        geoDataClient = Places.getGeoDataClient(this, null);

        AutocompleteFilter filter =
                new AutocompleteFilter.Builder().setCountry("PH").build();

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, latLngBounds, filter);

        textVieworigin.setAdapter(placeAutocompleteAdapter);
        textViewdestination.setAdapter(placeAutocompleteAdapter);

        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
                HideSoftKeyboard();
            }
        });

    }

    private void setTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MapsActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                departureHour = hourOfDay;
                departureMinute = minute;
                //Toast.makeText(MapsActivity.this, hourOfDay + minute, Toast.LENGTH_SHORT).show();
            }
        }, hours, minutes, false);
        timePickerDialog.show();
    }

    private void geoLocate(){
        originString = textVieworigin.getText().toString();
        destinationString = textViewdestination.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(originString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address originAddress = list.get(0);

        try{
            list = geocoder.getFromLocationName(destinationString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address destinationAddress = list.get(0);

        origin = new LatLng(originAddress.getLatitude(), originAddress.getLongitude());
        destination = new LatLng(destinationAddress.getLatitude(), destinationAddress.getLongitude());

        mark(originAddress, destinationAddress);

        getRoute(originAddress, destinationAddress);

        buttonCreate.setVisibility(View.VISIBLE);
        buttonCreate.setOnClickListener(v -> {
            CreateTravel createTravel = new CreateTravel();
            String key = createTravel.create(origin, destination, originString, destinationString, fareFrom, fareTo, departureHour, departureMinute, estimatedTravelTime);
            Intent intent = new Intent(MapsActivity.this, InsideRoomActivity.class);
            intent.putExtra("id", key);
            startActivity(intent);
        });

    }

    private void getRoute(Address origin, Address destination){
        Point originPoint = Point.fromLngLat(origin.getLongitude(), origin.getLatitude());
        Point destinationPoint = Point.fromLngLat(destination.getLongitude(), destination.getLatitude());

        NavigationRoute.builder(this)
                .accessToken("pk.eyJ1IjoiZ29yaWtvIiwiYSI6ImNqbXhlMmU3cDFuc2wzcXM4MmV4aG5reHQifQ.JkqGov_XghkeZ_hmYEH8xg")
                .origin(originPoint)
                .destination(destinationPoint)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            Toast.makeText(MapsActivity.this, "No routes", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(response.body().routes().size() == 0){
                            Toast.makeText(MapsActivity.this, "No routes", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Integer count = response.body().routes().size();

                        for(int x=0; x<=2 && x<response.body().routes().size(); x++){
                            DirectionsRoute currentRoute = response.body().routes().get(x);
                            Double distance = currentRoute.distance() / 1000;//in meters
                            Double duration = currentRoute.duration() / 60;//in seconds

                            List<Point> points = PolylineUtils.decode(currentRoute.geometry(),6);

                            PolylineOptions options = new PolylineOptions()
                                    .width(5)
                                    .geodesic(true);
                            if(x == 0){
                                options.color(Color.BLUE);
                                Double fare = (distance*13.50)+(duration*2)+40;
                                fareFrom = Integer.valueOf(fare.intValue())-20;
                                fareTo = Integer.valueOf(fare.intValue())+20;
                                estimatedTravelTime = Integer.valueOf(duration.intValue());
                                textView.setText("Estimated fare: PHP " + fareFrom.toString() + " - PHP " + fareTo.toString());
                            }else{
                                options.color(Color.GRAY);
                            }

                            for(int i = 0; i<points.size(); i++){
                                options.add(new LatLng(points.get(i).latitude(), points.get(i).longitude()));
                            }

                            mMap.addPolyline(options);
                        }
                        Toast.makeText(MapsActivity.this, "routes found: " + count, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(MapsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void mark(Address origin, Address destination){
        LatLng latLngOrigin = new LatLng(origin.getLatitude(), origin.getLongitude());
        LatLng latLngDestination = new LatLng(destination.getLatitude(), destination.getLongitude());

        MarkerOptions markerOrigin = new MarkerOptions()
                .position(latLngOrigin)
                .title("Origin Address");

        MarkerOptions markerDestination = new MarkerOptions()
                .position(latLngDestination)
                .title("Destination Address");

        mMap.addMarker(markerOrigin);
        mMap.addMarker(markerDestination);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }

        init();
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if (currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(16));

        if (googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void HideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
