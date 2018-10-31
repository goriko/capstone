package com.example.dar.systemdesign;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dar.systemdesign.NavBarActivity.sContext;

public class CreateRoomActivity extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

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
    private TextView textViewFare;
    private TextView textViewTravel;
    private TextView textViewDeparture;
    private View rootView;
    private Fragment fragment = null;
    private int i = 0;

    private LatLng origin;
    private LatLng destination;
    private String originString;
    private String destinationString;
    private Integer fareFrom;
    private Integer fareTo;
    private Integer estimatedTravelTime;
    private Integer departureHour;
    private Integer departureMinute;
    private PolylineOptions route = new PolylineOptions()
            .width(5)
            .geodesic(true);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_room, container, false);
        textVieworigin = (AutoCompleteTextView) rootView.findViewById(R.id.editTextOrigin);
        textViewdestination = (AutoCompleteTextView) rootView.findViewById(R.id.editTextDestination);
        buttonFind = (Button) rootView.findViewById(R.id.buttonFind);
        buttonCreate = (Button) rootView.findViewById(R.id.buttonCreate);
        buttonTime = (Button) rootView.findViewById(R.id.buttonTime);
        textViewFare = (TextView) rootView.findViewById(R.id.textViewFare);
        textViewTravel = (TextView) rootView.findViewById(R.id.textViewTravel);
        textViewDeparture = (TextView) rootView.findViewById(R.id.textViewDeparture);

        SupportMapFragment mapFragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (ContextCompat.checkSelfPermission(sContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    buildGoogleApiClient();

                    mMap.setMyLocationEnabled(true);
                }

                init();
            }
        });

        buttonCreate.setOnClickListener(v -> {
            CreateTravel createTravel = new CreateTravel();
            String key = createTravel.create(origin, destination, originString, destinationString, fareFrom, fareTo, departureHour, departureMinute, estimatedTravelTime);
            fragment = new InsideRoomActivity(key);
            replaceFragment(fragment);
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(sContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(sContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(sContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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
                    Toast.makeText(sContext, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void init() {
        geoDataClient = Places.getGeoDataClient(sContext, null);

        AutocompleteFilter filter =
                new AutocompleteFilter.Builder().setCountry("PH").build();

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(sContext, geoDataClient, latLngBounds, filter);

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

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                departureHour = hourOfDay;
                departureMinute = minute;
                if(departureHour > 12){
                    textViewDeparture.setText(departureHour-12+":"+departureMinute+" pm");
                }else{
                    textViewDeparture.setText(departureHour+":"+departureMinute+" am");
                }
                if(i == 0){
                    Toast.makeText(sContext, "Add Origin and Destination", Toast.LENGTH_SHORT).show();
                    i = 1;
                }else{
                    buttonCreate.setVisibility(View.VISIBLE);
                }
            }
        }, hours, minutes, false);
        timePickerDialog.show();
    }

    private void geoLocate(){
        originString = textVieworigin.getText().toString();
        destinationString = textViewdestination.getText().toString();
        Geocoder geocoder = new Geocoder(sContext);
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

        if(i == 0){
            Toast.makeText(sContext, "Add Departure Time", Toast.LENGTH_SHORT).show();
            i = 1;
        }else{
            buttonCreate.setVisibility(View.VISIBLE);
        }
    }

    private void getRoute(Address origin, Address destination){
        Point originPoint = Point.fromLngLat(origin.getLongitude(), origin.getLatitude());
        Point destinationPoint = Point.fromLngLat(destination.getLongitude(), destination.getLatitude());

        NavigationRoute.builder(sContext)
                .accessToken("pk.eyJ1IjoiZ29yaWtvIiwiYSI6ImNqbXhlMmU3cDFuc2wzcXM4MmV4aG5reHQifQ.JkqGov_XghkeZ_hmYEH8xg")
                .origin(originPoint)
                .destination(destinationPoint)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            Toast.makeText(sContext, "No routes", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(response.body().routes().size() == 0){
                            Toast.makeText(sContext, "No routes", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Integer count = response.body().routes().size();

                        for(int x=0; x<=2 && x<response.body().routes().size(); x++){
                            DirectionsRoute currentRoute = response.body().routes().get(x);
                            Double distance = currentRoute.distance() / 1000;//in meters
                            Double duration = currentRoute.duration() / 60;//in seconds

                            Double fare = (distance*13.50)+(duration*2)+40;
                            fareFrom = Integer.valueOf(fare.intValue())-20;
                            if(fareFrom <= 45){
                                fareFrom = 45;
                            }
                            fareTo = Integer.valueOf(fare.intValue())+20;
                            estimatedTravelTime = Integer.valueOf(duration.intValue());

                            List<Point> points = PolylineUtils.decode(currentRoute.geometry(),6);

                            PolylineOptions options = new PolylineOptions()
                                    .width(5)
                                    .geodesic(true);
                            if(x == 0){
                                for(int i = 0; i<points.size(); i++){
                                    route.add(new LatLng(points.get(i).latitude(), points.get(i).longitude()));
                                }
                                route.color(Color.BLUE);
                                textViewTravel.setText(estimatedTravelTime.toString()+"minute(s)");
                                textViewFare.setText("Php " + fareFrom.toString() + " - PHP " + fareTo.toString());
                            }else{
                                for(int i = 0; i<points.size(); i++){
                                    options.add(new LatLng(points.get(i).latitude(), points.get(i).longitude()));
                                }
                                options.color(Color.GRAY);
                                mMap.addPolyline(options);
                            }
                        }
                        mMap.addPolyline(route);
                        Toast.makeText(sContext, "routes found: " + count, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(sContext, "Failed", Toast.LENGTH_SHORT).show();
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

    private void HideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(sContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
