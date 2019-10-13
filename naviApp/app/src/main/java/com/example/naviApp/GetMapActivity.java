package com.example.naviApp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
<<<<<<< Updated upstream
=======
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
>>>>>>> Stashed changes

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

<<<<<<< Updated upstream
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
=======
        setupGoogleMapScreenSettings(googleMap);
//        LatLng origin = new LatLng(Constants.userLatitude, Constants.userLongitude);
//        LatLng destination = new LatLng(47.759138, -122.191164);

        try {
            List address = new Geocoder(this).getFromLocation(Constants.userLatitude, Constants.userLongitude, 1);
            Address[] arr = (Address[]) address.toArray(new Address[0]);
//            Log.d("MAP", arr[0].toString());
//            Log.d("MAP", arr[1].toString());
//            "18115 Campus Way NE Bothell WA 98011"
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
                for(int i = 0; i < 3; i ++){
                    if(polyArr[i] == polyline){
                        index = i;
                    }
                }
//        results.routes[0].legs[0].distance
                //        stations = response.getJSONArray("stations");
                double distanceKilometers = (double)results.routes[index].legs[0].distance.inMeters / 1000;
                double distanceMiles = 0.621371 * distanceKilometers;
                double gasCost = (distanceMiles / Constants.averageMPG) * Constants.gasPrice;

                String gasInfo = "Gas costs: $" + gasCost;

            }
        });
>>>>>>> Stashed changes
    }
}
