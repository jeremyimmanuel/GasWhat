package com.example.naviApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText makeInput;
    private EditText modelInput;
    private EditText yearInput;

//    private String make;
//    private String model;
//    private String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeInput = (EditText) findViewById(R.id.make);
        modelInput = (EditText) findViewById(R.id.model);
        yearInput = (EditText) findViewById(R.id.year);

    }

    public void enterVehicle(View view){
        Constants.make = makeInput.getText().toString();
        Constants.model = modelInput.getText().toString();
        Constants.year = yearInput.getText().toString();

        Intent getVehicleIntent = new Intent(this, getVehicle.class);
        startActivity(getVehicleIntent);
    }


}
