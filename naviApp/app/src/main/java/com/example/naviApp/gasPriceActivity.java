package com.example.naviApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class gasPriceActivity extends AppCompatActivity {

    String result;
    double averageGasPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_price);

        final TextView textView = findViewById(R.id.gasView);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.gasPriceURL;

//        Log.d("URL", Constants.vehicleID);
        String latitude = "47.608013";
        String longitude = "-122.335167";
        String distance = "30"; // in miles

        String fuelType = "reg"; // option among reg, mid, diesel and pre
        String sortBy = "distance";
        averageGasPrice = -1.0;

        url = url + "stations/radius/" + latitude + "/"
                + longitude + "/" + distance + "/" + fuelType + "/" + sortBy
                + "/" + Constants.gasAPIkey + ".json?";

        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        parseXML(response);
//                        textView.setText(result);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);

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


    }

    public void enterDestination(View view){

        Intent getDestinationIntent = new Intent(this, DestinationActivity.class);
        startActivity(getDestinationIntent);

    }

}
