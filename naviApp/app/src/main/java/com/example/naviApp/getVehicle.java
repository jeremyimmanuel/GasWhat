package com.example.naviApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class getVehicle extends AppCompatActivity {

    Hashtable<String, String> vehicles = new Hashtable<String, String>();
    String[] vehicleKeys;
    Spinner vehicleChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vehicle);
        vehicleChoices = (Spinner) findViewById(R.id.vehicleSpinner);

//        final TextView textView = (TextView) findViewById(R.id.vehicleChoices);
        // ...

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.fuelEconomyURL;
        url = url + "vehicle/menu/options?year=" + Constants.year + "&make=" +
            Constants.make + "&model=" + Constants.model;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseXML(response);

//                        textView.setText(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void parseXML(String xml) {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsing(parser);

        } catch (XmlPullParserException e) {

        } catch (IOException e) {
        }

//        return null;
    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        int eventType = parser.getEventType();
        String currentVehicle = null;
        String currentID = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
//                    Log.d("STATE", eltName);

                    if ("text".equals(eltName)) {
                        currentVehicle = parser.nextText();
//                        Log.d("STATE", eltName);

                    } else if (currentVehicle != null) {
                        if ("value".equals(eltName)) {
                            currentID = parser.nextText();
//                            Log.d("STATE", eltName);
                            vehicles.put(currentVehicle, currentID);
                        }
                    }

                    break;
            }

            eventType = parser.next();
        }

        printVehicles(vehicles);
    }

    private void printVehicles(Hashtable<String, String> vehicles) {

        Set<String> keys = vehicles.keySet();
        vehicleKeys = keys.toArray(new String[keys.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item, vehicleKeys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleChoices.setAdapter(adapter);
    }

    public void enterMPG(View view){
//        String key = vehicleChoices.getSelectedItem().toString();
        Log.d("GAS", vehicles.get(vehicleChoices.getSelectedItem().toString()));
        Constants.vehicleID = vehicles.get(vehicleChoices.getSelectedItem().toString());

        Intent getMPGIntent = new Intent(this, getMPG.class);
        startActivity(getMPGIntent);
    }
}
