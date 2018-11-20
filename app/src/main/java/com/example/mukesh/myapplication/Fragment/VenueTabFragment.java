package com.example.mukesh.myapplication.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.VenueTabInfo;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class VenueTabFragment extends Fragment {
    GetVenueTabDetails getVenueTabDetails;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public VenueTabFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_tab, container, false);
        String venueTabInfoStr = getArguments().getString("venueTabInfo");

        if (Objects.isNull(venueTabInfoStr)) {
            return view;
        }
        VenueTabInfo eventDetail = new Gson().fromJson(venueTabInfoStr, VenueTabInfo.class);
        Log.i("eventDetail", new Gson().toJson(eventDetail));

        return view;
    }


}


class GetVenueTabDetails extends AsyncTask<String, Integer, String> {
    EventDetails eventDetail;
    VenueTabInfo venueTabInfo;

    @Override
    protected String doInBackground(String... strings) {

        eventDetail = new Gson().fromJson(strings[0], EventDetails.class);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://100.26.198.168:3000/api/ticketmaster/geteventbyid/");
        urlBuilder.append(eventDetail.getEventId());

        StringBuilder eventTabResultBuilder = new StringBuilder();
        Log.i("Hitting Url  for event tab result... ", urlBuilder.toString());
        try {
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                eventTabResultBuilder.append(line);
            }

            rd.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventTabResultBuilder.toString();
    }

    @Override
    protected void onPostExecute(String eventTabResults) {
        Log.i("", eventTabResults);
        venueTabInfo = new VenueTabInfo();
        try {
            JSONObject EventDetailsJson = new JSONObject(eventTabResults);

            JSONObject venueJson = EventDetailsJson.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);

            // Building Venue Object
            venueTabInfo.setVenueName(venueJson.getString("name"));
            venueTabInfo.setAddress(venueJson.getJSONObject("address").getString("line1"));
            venueTabInfo.setCity(venueJson.getJSONObject("city").getString("name") + " " + venueJson.getJSONObject("state").getString("name"));

            if (venueJson.optJSONObject("boxOfficeInfo") != null) {
                venueTabInfo.setPhoneNumber(venueJson.optJSONObject("boxOfficeInfo").optString("phoneNumberDetail"));
                venueTabInfo.setOpenHours(venueJson.optJSONObject("boxOfficeInfo").optString("openHoursDetail"));
            }
            if (venueJson.optJSONObject("generalInfo") != null) {
                venueTabInfo.setGeneralRule(venueJson.optJSONObject("generalInfo").optString("generalRule"));
                venueTabInfo.setChildRule(venueJson.optJSONObject("generalInfo").optString("childRule"));
            }
            venueTabInfo.setLat(venueJson.getJSONObject("location").getString("longitude"));
            venueTabInfo.setLon(venueJson.getJSONObject("location").getString("latitude"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
