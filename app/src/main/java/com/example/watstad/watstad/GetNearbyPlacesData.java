package com.example.watstad.watstad;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.security.AccessController.getContext;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    HomeFragment homeFragment = new HomeFragment();
    MainActivity mainActivity = new MainActivity();




    String googlePlacesData;
    GoogleMap mgoogleMap;
    String url;
    Boolean isVisited = false;
//    float[] result = new float[1];

    String poiContent = "De Markt is een plein in de binnenstad van Maastricht. Het plein ontleent zijn naam aan de warenmarkten die hier al eeuwenlang plaatsvinden. Tevens bevindt zich op de Markt het Stadhuis van Maastricht en een groot aantal horecagelegenheden. De Markt is goed bereikbaar met het openbaar vervoer.";
    String poiTitle = "Mestreechter merret";
    String poiDate = "26-9-2018";
    //String poiAchievement = "Maastricht Pathfinder";


    @Override
    protected String doInBackground(Object... objects) {
        mgoogleMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser parser = new DataParser();
        nearbyPlacesList = parser.parse(s);
        showNearbyPlaces(nearbyPlacesList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
        for (int i = 0; i<nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            String details = googlePlace.get("details");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));


            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mgoogleMap.addMarker(markerOptions);


           // notificationCall(placeName);

        }
    }



}
