package com.example.mukesh.myapplication.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mukesh.myapplication.POJO.SearchForm;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.Services.GPSTracker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public SearchForm searchform;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = findViewById(R.id.category_spinner);

        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Music");
        categories.add("Sports");
        categories.add("Arts & Theatre");
        categories.add("Film");
        categories.add("Miscellaneous");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        Spinner unitSpinner = findViewById(R.id.unit_spinner);

        List<String> distanceUnit = new ArrayList<>();
        distanceUnit.add("Miles");
        distanceUnit.add("Kilometer");
        ArrayAdapter<String> unitDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distanceUnit);
        unitDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitDataAdapter);

        findViewById(R.id.keyword).setEnabled(true);

        findViewById(R.id.keyword).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.keyword_error).setVisibility(View.INVISIBLE);
                return true;
            }
        });

        findViewById(R.id.location_desc).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.location_error).setVisibility(View.INVISIBLE);
                return true;
            }
        });

        findViewById(R.id.from_here).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.location_desc).setEnabled(false);
                findViewById(R.id.location_error).setVisibility(View.INVISIBLE);
            }

        });

        findViewById(R.id.from_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.location_desc).setEnabled(true);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        }

    }

    public void searchEvents(View view) {
        RadioGroup radioGroup;

        Pattern p = Pattern.compile("[^a-zA-Z0-9 ]");
        boolean isInValidInput = false;
        String keyword = ((EditText) findViewById(R.id.keyword)).getText().toString();
        if (keyword == null || keyword.length() <= 0 || p.matcher(keyword).find()) {
            findViewById(R.id.keyword_error).setVisibility(View.VISIBLE);
            isInValidInput = true;
        }

        boolean isLocaDecPresent = false;
        if (findViewById(R.id.location_desc).isEnabled()) {
            String locaDec = ((EditText) findViewById(R.id.location_desc)).getText().toString();
            if (locaDec == null || locaDec.length() <= 0) {
                findViewById(R.id.location_error).setVisibility(View.VISIBLE);
                isInValidInput = true;
            }
            isLocaDecPresent = true;
        }
        if (isInValidInput) {
            Toast.makeText(view.getContext(), "Please fix all fields with errors",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Spinner spinner = findViewById(R.id.category_spinner);
        String category = spinner.getSelectedItem().toString();

        int distance = Integer.valueOf(((EditText) findViewById(R.id.distance)).getText().toString());
        spinner = findViewById(R.id.unit_spinner);
        String unit = spinner.getSelectedItem().toString();

        searchform = new SearchForm();
        searchform.setKeyword(keyword);
        searchform.setCategory(category);
        searchform.setDistance(distance);
        searchform.setDistanceUnit(unit);

        radioGroup = findViewById(R.id.radioLocation);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (((RadioButton) findViewById(selectedId)).getText().toString().contains("Other")) {
            searchform.setOtherLocation(true);
        } else {
            GPSTracker gps = new GPSTracker(view.getContext());
            if (gps != null) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                searchform.setLat(latitude);
                searchform.setLon(longitude);
            }
        }

        if (isLocaDecPresent) {
            searchform.setLocationDescription(((EditText) findViewById(R.id.location_desc)).getText().toString());
        }


        Intent newIntent = new Intent(this, EventDetailsPage.class);
        newIntent.putExtra("searchForm", new Gson().toJson(searchform));
        searchform = new Gson().fromJson(getIntent().getStringExtra("searchForm"), SearchForm.class);
        startActivity(newIntent);

    }

}

