package com.example.mukesh.myapplication.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mukesh.myapplication.DelayedProgressDialog;
import com.example.mukesh.myapplication.EventDetailsAdaptor;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.SearchForm;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsPage extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    EventDetailsAdaptor adaptor;
    List<EventDetails> eventDetails = new ArrayList<>();
    DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    Map<String, String> categoryToIdMap = new HashMap<>();
    SearchForm searchform;

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onResume();
        setContentView(R.layout.activity_show_message);
        progressDialog.show(getSupportFragmentManager(), "Loading ...");

        searchform = new Gson().fromJson(getIntent().getStringExtra("searchForm"), SearchForm.class);

        categoryToIdMap.put("All", "ALL");
        categoryToIdMap.put("Music", "KZFzniwnSyZfZ7v7nJ");
        categoryToIdMap.put("Sports", "KZFzniwnSyZfZ7v7nE");
        categoryToIdMap.put("Arts & Theatre", "KZFzniwnSyZfZ7v7na");
        categoryToIdMap.put("Film", "KZFzniwnSyZfZ7v7nn");
        categoryToIdMap.put("Miscellaneous", "KZFzniwnSyZfZ7v7n1");


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringBuffer sBuf = new StringBuffer();
        sBuf.append("http://100.26.198.168:3000/api/ticketmaster/getevents/");
        sBuf.append(searchform.keyword).append("/");
        sBuf.append(categoryToIdMap.get(searchform.category)).append("/");
        sBuf.append(searchform.distance).append("/");
        sBuf.append("dr5regw3p").append("/");
        sBuf.append(searchform.distanceUnit.toLowerCase());

        Log.i("Hitting Url ... ", sBuf.toString());


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sBuf.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                        adaptor = new EventDetailsAdaptor(eventDetails, getApplicationContext());
                        recyclerView.setAdapter(adaptor);
                        progressDialog.cancel();
                    }

                    private void processResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray eventArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                            for (int idx = 0; idx < eventArray.length(); idx++) {
                                EventDetails eventInfo = new EventDetails();

                                eventInfo.setEventName(eventArray.getJSONObject(idx).getString("name"));
                                eventInfo.setEventDate(eventArray.getJSONObject(idx).getJSONObject("dates").getJSONObject("start").getString("localDate"));
                                eventInfo.setEventVenue(eventArray.getJSONObject(idx).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
                                eventInfo.setEventId(eventArray.getJSONObject(idx).getString("id"));
                                eventInfo.setEventType(eventArray.getJSONObject(idx).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
                                eventDetails.add(eventInfo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

    }


}
