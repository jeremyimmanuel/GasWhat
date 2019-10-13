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
import java.util.Hashtable;
import java.util.Set;

public class getMPG extends AppCompatActivity {

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_mpg);

        final TextView textView = (TextView) findViewById(R.id.mpgList);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.fuelEconomyURL;
//        Log.d("URL", Constants.vehicleID);
        url = url + "vehicle/" + Constants.vehicleID;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseXML(response);
                        textView.setText(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
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
//        String currentMPH = null;
//        String currentHighwayMPH = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();

                    if ("city08".equals(eltName)) {
                        Constants.cityMPG = parser.nextText();

                    } else if ("highway08".equals(eltName)) {
                        Constants.highwayMPG = parser.nextText();
                    } else if ("comb08".equals(eltName)){
                        Constants.averageMPG = Double.parseDouble(parser.nextText());
                    }


                    break;
            }

            eventType = parser.next();
        }

        printMPH();
    }

    private void printMPH() {
        result = "CITY MPH = " + Constants.cityMPG + "\n" + "HIGHWAY MPH = " + Constants.highwayMPG;
    }


    public void enterGetGasPrice(View view){
        //String key = vehicleChoices.getSelectedItem().toString();
        //Constants.vehicleID = vehicles.get(vehicleChoices.getSelectedItemPosition());

        Intent getGasIntent = new Intent(this, gasPriceActivity.class);
        startActivity(getGasIntent);

    }
}
