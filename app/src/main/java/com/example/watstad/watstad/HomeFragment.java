package com.example.watstad.watstad;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.constraint.Constraints.TAG;

public class HomeFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public MainActivity mainActivity;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;

//    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final int PERMISSION_REQUEST_LOCATION_CODE = 99;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17;
    int PROXIMITY_RADIUS = 300; // 200 is fine radius
    //    double latitude = 51.450851;
//    double longitude = 5.480200;
    public double latitude;
    public double longitude;

    public Boolean isMarkerShown = false;

    String poiContent = "De Markt is een plein in de binnenstad van Maastricht. Het plein ontleent zijn naam aan de warenmarkten die hier al eeuwenlang plaatsvinden. Tevens bevindt zich op de Markt het Stadhuis van Maastricht en een groot aantal horecagelegenheden. De Markt is goed bereikbaar met het openbaar vervoer.";
    String poiTitle = "Mestreechter merret";
    String poiDate = "01-11-2018";



    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationPermissionsGranted = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        return mView;


    }


    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mainActivity = (MainActivity) getActivity();
        getLocationPermission();
        //AskPermission();
        //buildGoogleApiClient();
        //initMap();


    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(mainActivity,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(mainActivity,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(mainActivity,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(mainActivity,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");


        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            Log.d(TAG, "Koekje: " + currentLocation.getLongitude() + ", " + currentLocation.getLatitude());

                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();


                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            //Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        mMapView = getView().findViewById(R.id.gMap1);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(mainActivity, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mGoogleMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            buildGoogleApiClient();

            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);
            customLocations();
            notificationCall();

            try {
                boolean success = mGoogleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                mainActivity, R.raw.maps_style));

                if (!success) {
                    Log.e("Loading map", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("Loading map", "Can't find style. Error: ", e);
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Dialog dialog = new Dialog(mainActivity);
                dialog.setContentView(R.layout.dialog_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView textViewName = dialog.findViewById(R.id.textViewName);
                textViewName.setText(poiTitle);

                TextView textViewContent = dialog.findViewById(R.id.textViewContent);
                textViewContent.setText(poiContent);

                TextView textViewDate = dialog.findViewById(R.id.textViewDate);
                textViewDate.setText("Visited on " + poiDate);

//                    TextView textViewAchievement = dialog.findViewById(R.id.textViewAchievement);
//                    textViewAchievement.setText("Part of " + poiAchievement);


                dialog.show();

                ImageButton imageButtonClose = dialog.findViewById(R.id.imageButtonClose);

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    public void customLocations() {

        //fontys
        double lat = 51.4520885;
        double lng = 5.4819826;

        MarkerOptions fontysMarker = new MarkerOptions();
        LatLng latlng = new LatLng(lat, lng);

        fontysMarker.position(latlng);
        fontysMarker.title("Fontys Rachelsmolen");
        fontysMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentLocationMarker = mGoogleMap.addMarker(fontysMarker);


    }

    public void showNearbyPlaces(String search) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        String url = getUrl(latitude, longitude, search);
        dataTransfer[0] = mGoogleMap;
        dataTransfer[1] = url;
        Log.d(TAG, "locationnboy: " + latitude + ", " + longitude);
        getNearbyPlacesData.execute(dataTransfer);
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);

        googlePlaceUrl.append("&key="+"AIzaSyBtKXs8q3AYIhL3vjKxwgLNDzPhYF4vUmU");

        Log.d(TAG, "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(mainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        //lastLocation = location;
        Log.d(TAG, "locationnn:" + latitude + ", " + longitude);
//        if (currentLocationMarker != null) {
//            currentLocationMarker.remove();
//        }

        Log.d(TAG, "koekje3:" + location + ", " + latitude + ", " + longitude);

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        Log.d(TAG, "koekje4: " + isMarkerShown);

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latlng);
//        markerOptions.title("Current Location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        currentLocationMarker = mGoogleMap.addMarker(markerOptions);

        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(17));

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            Log.d(TAG, "yowaddup: " + "yoyo");
                            onLocationChanged(locationResult.getLastLocation());
                            showNearbyPlaces("art_gallery|cemetery|church|city_hall|courthouse|embassy|hindu_temple|library|mosque|museum|park|shopping_mall|stadium|synagogue|train_station|zoo|school");

//                            if (isMarkerShown) {
//                                notificationCall();
//                                isMarkerShown = false;
//                            }
                        }

                    },
                    Looper.myLooper());

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void notificationCall() {
        createNotificationChannel();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mainActivity, "my_channel")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.watstad_logo_small)
                .setContentTitle("New location discovered!")
                .setContentText("You have discovered: Fontys Hogeschool Eindhoven!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications from watSTAD?!";
            String description = "Notifications when you discover a new location";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel("my_channel", name, importance);

            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}