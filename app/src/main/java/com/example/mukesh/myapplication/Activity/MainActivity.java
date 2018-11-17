package com.example.mukesh.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mukesh.myapplication.POJO.SearchForm;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    }

    public void searchEvents(View view) {

        String keyword = ((EditText) findViewById(R.id.keyword)).getText().toString();

        Spinner spinner = findViewById(R.id.category_spinner);
        String category = spinner.getSelectedItem().toString();

        int distance = Integer.valueOf(((EditText) findViewById(R.id.distance)).getText().toString());
        spinner = findViewById(R.id.unit_spinner);
        String unit = spinner.getSelectedItem().toString();

        SearchForm searchForm = new SearchForm();
        searchForm.setKeyword(keyword);
        searchForm.setCategory(category);
        searchForm.setDistance(distance);
        searchForm.setDistanceUnit(unit);


        Intent newIntent = new Intent(this, EventDetailsPage.class);
        newIntent.putExtra("searchForm", new Gson().toJson(searchForm));
        startActivity(newIntent);

    }
}
