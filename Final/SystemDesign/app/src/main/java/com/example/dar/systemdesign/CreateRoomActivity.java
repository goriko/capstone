package com.example.dar.systemdesign;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dar.systemdesign.NavBarActivity.sContext;

public class CreateRoomActivity extends Fragment implements OnMapReadyCallback,
        LocationEngineListener,
        PermissionsListener {

    private View rootView;

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location userLocation;

    private Button buttonTime;
    private Button buttonCreate;
    private Button buttonFind;
    private TextView textViewDeparture;
    private TextView textViewFare;
    private TextView textViewTravel;
    private AutoCompleteTextView textVieworigin;
    private AutoCompleteTextView textViewdestination;

    private Integer departureHour;
    private Integer departureMinute;
    private int i = 0;
    private GeoDataClient geoDataClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private static final LatLngBounds latLngBounds = new LatLngBounds(new com.google.android.gms.maps.model.LatLng(-40, -168), new com.google.android.gms.maps.model.LatLng(71, 136));
    private String originString;
    private String destinationString;
    private LatLng origin;
    private LatLng destination;
    private Marker markerOrigin = null;
    private Marker markerDestination = null;
    private Integer fareFrom;
    private Integer fareTo;
    private Integer estimatedTravelTime;
    private PolylineOptions route = new PolylineOptions()
            .width(5);
    private Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_room, container, false);
        Mapbox.getInstance(getActivity(), getString(R.string.access_token));
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.getMapAsync(this::onMapReady);

        buttonTime = (Button) rootView.findViewById(R.id.buttonTime);
        buttonCreate = (Button) rootView.findViewById(R.id.buttonCreate);
        buttonFind = (Button) rootView.findViewById(R.id.buttonFind);
        textViewDeparture = (TextView) rootView.findViewById(R.id.textViewDeparture);
        textViewFare = (TextView) rootView.findViewById(R.id.textViewFare);
        textViewTravel = (TextView) rootView.findViewById(R.id.textViewTravel);
        textVieworigin = (AutoCompleteTextView) rootView.findViewById(R.id.editTextOrigin);
        textViewdestination = (AutoCompleteTextView) rootView.findViewById(R.id.editTextDestination);


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

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTravel createTravel = new CreateTravel();
                String key = createTravel.create(origin, destination, originString, destinationString, fareFrom, fareTo, departureHour, departureMinute, estimatedTravelTime);
                fragment = new InsideRoomActivity(key, "no");
                replaceFragment(fragment);
            }
        });

        return rootView;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                                    .width(5);

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
                                map.addPolyline(options);
                            }
                        }
                        map.addPolyline(route);
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

        if(markerOrigin != null || markerDestination != null){
            map.removeMarker(markerOrigin);
            map.removeMarker(markerDestination);
        }

        markerOrigin = map.addMarker(new MarkerOptions().position(latLngOrigin).title("Origin Address"));
        markerDestination = map.addMarker(new MarkerOptions().position(latLngDestination).title("Destination Address"));

    }

    private void HideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();
    }

    private void enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(sContext)){
            initializeLocationEngine();
            initializeLocationLayer();
        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    private void initializeLocationEngine(){
        locationEngine = new LocationEngineProvider(sContext).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        @SuppressLint("MissingPermission") Location lastLocation = locationEngine.getLastLocation();
        if(lastLocation !=null){
            userLocation = lastLocation;
            setCameraPosition(lastLocation);
        }else{
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationLayer(){
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }
    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.0));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            userLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
