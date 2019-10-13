package com.example.naviApp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import org.joda.time.DateTime;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.fragment.app.FragmentActivity;

public class GetMapActivity extends FragmentActivity
    implements OnMapReadyCallback{

    private static final int overview = 0;
    private Polyline[] polyArr = new Polyline[3];
    private DirectionsResult results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private DirectionsResult getDirectionsDetails(String origin,String destination,TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .alternatives(true)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        setupGoogleMapScreenSettings(googleMap);
//        LatLng origin = new LatLng(Constants.userLatitude, Constants.userLongitude);
//        LatLng destination = new LatLng(47.759138, -122.191164);

        try {
            List address = new Geocoder(this).getFromLocation(Constants.userLatitude, Constants.userLongitude, 1);
            Address[] arr = (Address[]) address.toArray(new Address[0]);
            Log.d("MAP", arr[0].toString());
//            Log.d("MAP", arr[1].toString());

            results = getDirectionsDetails(arr[0].getAddressLine(0), Constants.destination,TravelMode.DRIVING);

            if (results != null) {
                addPolyline(results, googleMap);
                positionCamera(results.routes[overview], googleMap);
                addMarkersToMap(results, googleMap);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {

                // return String?
                int index = 0;
                for(int i = 0; i < 3 && i < results.routes.length; i++ ){
                    if(polyArr[i].getTag() == polyline.getTag()){
                        index = i;
                    }
                }
//        results.routes[0].legs[0].distance
                //        stations = response.getJSONArray("stations");

                double distanceKilometers = (double)results.routes[index].legs[0].distance.inMeters;
                double distanceMiles = 0.621371 * distanceKilometers / 1000.0;

                double gasCost = (distanceMiles / Constants.averageMPG) * Constants.gasPrice;
                gasCost = Math.round(gasCost * 100.0) / 100.0;
                String gasInfo = "Gas costs: $" + gasCost;
                Toast.makeText(getApplicationContext(), gasInfo, Toast.LENGTH_LONG).show();

            }
        });

    }


    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        //mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }


    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
        double midLat = (Constants.userLatitude + route.legs[overview].endLocation.lat)/2.0;
        double midLong = (Constants.userLongitude + route.legs[overview].endLocation.lng)/2.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(midLat, midLong), 10));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
//        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
//        PomMap.addPolyline(new PolylineOptions().addAll(decodedPath));

        for(int i = 0; i < 3 && i < results.routes.length; i++){
            List<LatLng> decodedPath2 = PolyUtil.decode(results.routes[i].overviewPolyline.getEncodedPath());
            polyArr[i] = mMap.addPolyline(new PolylineOptions().addAll(decodedPath2).clickable(true).width(30));
            polyArr[i].setTag(i);
        }
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

}
