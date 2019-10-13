package com.example.naviApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class DestinationActivity extends AppCompatActivity {

    private EditText inputDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        inputDestination = (EditText) findViewById(R.id.inputDestination);


    }

    public void enterMap(View view){
        Constants.destination = inputDestination.getText().toString();

        Intent getMapIntent = new Intent(this, GetMapActivity.class);
        startActivity(getMapIntent);
    }
}
