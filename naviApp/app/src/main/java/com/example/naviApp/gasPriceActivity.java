package com.example.naviApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class gasPriceActivity extends AppCompatActivity
        implements LocationListener {

    Intent intentThatCalled;
    public static double latitude;
    public static double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    TextView textView;
    String result;
    double averageGasPrice;
//    LocationManager lm;
//    LocationListener ll;
//    String latitude;
//    String longitud
//    Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_price);

        textView = findViewById(R.id.gasView);
//        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//
//
//        ll = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Toast.makeText(gasPriceActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
//
//                Constants.userLongitude = location.getLongitude();
//                Constants.userLatitude = location.getLatitude();
//
//                latitude = Double.toString(location.getLatitude());
//                longitude = Double.toString(location.getLongitude());
//
//                Log.d("LATLONG", latitude);
//                Log.d("LATLONG", longitude);
//                lm.removeUpdates(ll);
//                lm = null;
//
//                getGasPrices();
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//
//            }
//        };
//
//        Location userLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        if(userLocation == null){
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ll);
//        }
//
//        //user hasn't given permission
//
////        else
////        {
////
////
////
////        Constants.userLatitude = userLocation.getLatitude();
////        Constants.userLongitude = userLocation.getLongitude();
////
////        latitude = Double.toString(Constants.userLatitude);
////        longitude = Double.toString(Constants.userLongitude);
////
////        }
//
//
//
//
        getLocation();


    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {

                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Constants.userLatitude = latitude;
                Constants.userLongitude = longitude;

                Toast.makeText(this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                getGasPrices();
            }
            else{
//                Log.e("TEST", "here");
                locationManager.requestLocationUpdates(bestProvider, 2000, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        //Hey, a non null location! Sweet!

        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Constants.userLatitude = latitude;
        Constants.userLongitude = longitude;

        Log.d("LONGLAT", "" + latitude);
        Log.d("LONGLAT", "" + longitude);
        getGasPrices();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void getGasPrices() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.gasPriceURL;

//        Log.d("URL", Constants.vehicleID);

        //String latitude = "47.608013";
        //String longitude = "-122.335167";
        String distance = "30"; // in miles

        String fuelType = "reg"; // option among reg, mid, diesel and pre
        String sortBy = "distance";
        averageGasPrice = -1.0;

        Log.d("LATLONG", "" + latitude);
        Log.d("LATLONG", "" + longitude);

        url = url + "stations/radius/" + latitude + "/"
                + longitude + "/" + distance + "/" + fuelType + "/" + sortBy
                + "/" + Constants.gasAPIkey + ".json?";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray stations;
                        try {
                            double sum = 0.0;
                            int count = 0;
                            stations = response.getJSONArray("stations");
                            for(int i = 0; i < stations.length(); i++){
                                JSONObject station = stations.getJSONObject(i);
                                String price = station.getString("reg_price");
                                Log.d("LOG", price);
                                if(!price.equals("N/A")){
                                    sum += Double.parseDouble(price);
                                    count++;
                                }
                            }

                            if(count>0){
                                Log.d("here", "" +sum);
                                averageGasPrice = sum / (double) count;
                                Log.d("here", "" + averageGasPrice);
                            }
                            averageGasPrice = Math.round(averageGasPrice * 100.0) / 100.0;
                            result = "Close gas stations cost in average = $" + averageGasPrice;
                            textView.setText(result);

                            Constants.gasPrice = averageGasPrice;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("Error", "An error has occurred");
                    }
                });

        queue.add(jsonObjectRequest);

        Log.d("LATLONG", "" + latitude);
        Log.d("LATLONG", "" + longitude);
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//        {
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//            {
//                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, ll);
//            }
//        }
//    }

    public void enterMap(View view){

        Intent getMapIntent = new Intent(this, GetMapActivity.class);
        startActivity(getMapIntent);

    }


}
