package com.example.mukesh.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import com.example.mukesh.myapplication.EventDetailsAdaptor;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.SearchForm;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventDetailsPage extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static RecyclerView.LayoutManager layoutManager;
    public static EventDetailsAdaptor adaptor;
    public static List<EventDetails> eventDetails = new ArrayList<>();
    public static Context applicationCtx;

    public SearchForm searchform;
    public String geoHashCode;
    AlphaAnimation inAnimation;
    static AlphaAnimation outAnimation;

    static FrameLayout progressBarHolder;

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventDetailsPage.adaptor = new EventDetailsAdaptor(eventDetails, EventDetailsPage.applicationCtx);
        EventDetailsPage.recyclerView.setAdapter(EventDetailsPage.adaptor);
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Search Results");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchform = new Gson().fromJson(getIntent().getStringExtra("searchForm"), SearchForm.class);
        // Instantiate the RequestQueue.

        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        applicationCtx = getApplicationContext();

        if (Objects.isNull(searchform)) {
            return;
        }

        progressBarHolder = findViewById(R.id.progressBarHolder2);
        progressBarHolder.dispatchDisplayHint(View.VISIBLE);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        if (searchform.isOtherLocation()) {
            GetLatLonFromLocationDesc getLatLonFromLocationDesc = new GetLatLonFromLocationDesc();
            getLatLonFromLocationDesc.execute(new Gson().toJson(searchform));
        } else {
            GetHashCode getHashCode = new GetHashCode();
            getHashCode.execute(new Gson().toJson(searchform));
            geoHashCode = getHashCode.getGeoHashCode();
        }
    }
}


class GetEventResults extends AsyncTask<String, Integer, String> {

    public Map<String, String> categoryToIdMap = new HashMap<>();

    public List<EventDetails> getEventDetails() {
        return eventDetails;
    }

    public List<EventDetails> eventDetails = new ArrayList<>();


    @Override
    protected String doInBackground(String... strings) {

        categoryToIdMap.put("All", "ALL");
        categoryToIdMap.put("Music", "KZFzniwnSyZfZ7v7nJ");
        categoryToIdMap.put("Sports", "KZFzniwnSyZfZ7v7nE");
        categoryToIdMap.put("Arts & Theatre", "KZFzniwnSyZfZ7v7na");
        categoryToIdMap.put("Film", "KZFzniwnSyZfZ7v7nn");
        categoryToIdMap.put("Miscellaneous", "KZFzniwnSyZfZ7v7n1");

        StringBuffer sBuf = new StringBuffer();
        SearchForm searchForm = new Gson().fromJson(strings[0], SearchForm.class);
        sBuf.append("http://100.26.198.168:3000/api/ticketmaster/getevents/");
        sBuf.append(searchForm.keyword).append("/");
        sBuf.append(categoryToIdMap.get(searchForm.category)).append("/");
        sBuf.append(searchForm.distance).append("/");
        sBuf.append(searchForm.getGeoHashCode()).append("/");
        sBuf.append(searchForm.distanceUnit.toLowerCase());

        Log.i("Hitting Url ... ", sBuf.toString());
        String content = "", line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(sBuf.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line);
            }

            rd.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String response) {
        eventDetails = processResponse(response);
        EventDetailsPage.eventDetails = eventDetails;

        EventDetailsPage.adaptor = new EventDetailsAdaptor(eventDetails, EventDetailsPage.applicationCtx);
        EventDetailsPage.recyclerView.setAdapter(EventDetailsPage.adaptor);
        EventDetailsPage.progressBarHolder.setAnimation(EventDetailsPage.outAnimation);
        EventDetailsPage.progressBarHolder.setVisibility(View.GONE);

    }

    private List<EventDetails> processResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray eventArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
            for (int idx = 0; idx < eventArray.length(); idx++) {
                EventDetails eventInfo = new EventDetails();

                eventInfo.setEventName(eventArray.getJSONObject(idx).getString("name"));
                eventInfo.setEventDate(eventArray.getJSONObject(idx).getJSONObject("dates").getJSONObject("start").getString("localDate"));
                eventInfo.setEventVenue(eventArray.getJSONObject(idx).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
                eventInfo.setEventId(eventArray.getJSONObject(idx).getString("id"));
                eventInfo.setUrl(eventArray.getJSONObject(idx).getString("url"));
                eventInfo.setEventType(eventArray.getJSONObject(idx).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
                eventDetails.add(eventInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Intent newIntent = new Intent(EventDetailsPage.applicationCtx, EmptyEventResult.class);
            EventDetailsPage.applicationCtx.startActivity(newIntent);
        }
        return eventDetails;
    }
}

class GetHashCode extends AsyncTask<String, Integer, SearchForm> {

    public String getGeoHashCode() {
        return geoHashCode;
    }

    private String geoHashCode;

    public List<EventDetails> getEventDetails() {
        return eventDetails;
    }

    public List<EventDetails> eventDetails = new ArrayList<>();

    @Override
    protected SearchForm doInBackground(String... strings) {

        SearchForm searchForm = new Gson().fromJson(strings[0], SearchForm.class);

        StringBuffer geoHashCodeURL = new StringBuffer();
        geoHashCodeURL.append("http://100.26.198.168:3000/api/geohash/");
        geoHashCodeURL.append(searchForm.getLat()).append("/");
        geoHashCodeURL.append(searchForm.getLon());
        Log.i("Hitting Geo Hash Code ", geoHashCodeURL.toString());

        try {
            URL url = new URL(geoHashCodeURL.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = "", line;

            while ((line = rd.readLine()) != null) {
                content += line;
            }
            rd.close();
            connection.disconnect();

            Log.i("geoHashCodeURL", content);
            JSONObject json = new JSONObject(content);
            geoHashCode = json.getString("geoHash");
            searchForm.setGeoHashCode(geoHashCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchForm;
    }


    @Override
    protected void onPostExecute(SearchForm searchform) {
        GetEventResults getEventResults = new GetEventResults();
        getEventResults.execute(new Gson().toJson(searchform));
        eventDetails = getEventResults.getEventDetails();
    }

}

class GetLatLonFromLocationDesc extends AsyncTask<String, Integer, SearchForm> {

    public SearchForm getSearchForm() {
        return searchForm;
    }

    private SearchForm searchForm;

    public String getGeoHashCode() {
        return geoHashCode;
    }

    private String geoHashCode;

    @Override
    protected SearchForm doInBackground(String... strings) {

        searchForm = new Gson().fromJson(strings[0], SearchForm.class);

        StringBuffer locationURL = new StringBuffer();
        locationURL.append("http://100.26.198.168:3000/api/google/getlatlonbydescription/");

        Log.i("locationURL ", locationURL.toString());
        try {
            locationURL.append(URLEncoder.encode(searchForm.getLocationDescription(), "UTF-8"));
            URL url = new URL(locationURL.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Access-Control-Allow-Origin", "*");
            connection.connect();

            connection.getResponseCode();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = "", line;

            while ((line = rd.readLine()) != null) {
                content += line;
            }
            rd.close();
            connection.disconnect();

            Log.i("Lat Lon from Location description ", content);
            JSONObject json = new JSONObject(content);
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            searchForm.setLat(Double.parseDouble(location.getString("lat")));
            searchForm.setLon(Double.parseDouble(location.getString("lng")));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchForm;
    }

    @Override
    protected void onPostExecute(SearchForm searchform) {
        GetHashCode getHashCode = new GetHashCode();
        getHashCode.execute(new Gson().toJson(searchform));
        geoHashCode = getHashCode.getGeoHashCode();
        searchform.setGeoHashCode(geoHashCode);
    }
}
