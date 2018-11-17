package com.example.mukesh.myapplication.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mukesh.myapplication.R;

public class EventMoreDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_more_details);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void Back(View v) {
        onBackPressed();
    }
}
