package com.example.mukesh.myapplication.Fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.EventTabInfo;
import com.example.mukesh.myapplication.POJO.VenueTabInfo;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventTabFragment extends Fragment {
    public static View view;
    public static Context appContext;
    public static FragmentManager fragmentTransaction;
    public static EventTabFragment fragm;

    public EventTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_event_tab, container, false);

        String eventDetails = getArguments().getString("eventDetails");
        EventDetails eventDetail = new Gson().fromJson(eventDetails, EventDetails.class);

        fragmentTransaction = getFragmentManager();
        appContext = getActivity().getApplicationContext();
        fragm = EventTabFragment.this;

        new GetEventTabDetails().execute(eventDetails);
        TableLayout ll = view.findViewById(R.id.tableLayout);

        for (int i = 1; i < 10; i++) {
            TableRow tbrow = new TableRow(getActivity().getApplicationContext());
            tbrow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView tv1 = new TextView(getActivity().getApplicationContext());
            tv1.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv1.setId(i);

            tv1.setText("Test id: " + i);
            tbrow.addView(tv1);

            ll.addView(tbrow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }
}


class GetEventTabDetails extends AsyncTask<String, Integer, String> {

    EventDetails eventDetail;
    EventTabInfo eventTabInfo;
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
        eventTabInfo = new EventTabInfo();
        venueTabInfo = new VenueTabInfo();
        Random random;
        try {
            JSONObject EventDetailsJson = new JSONObject(eventTabResults);
            eventTabInfo.setEventName(EventDetailsJson.getString("name"));
            ;
            StringBuilder stringBuilder = new StringBuilder();
            JSONArray attractionsJson = EventDetailsJson.getJSONObject("_embedded").getJSONArray("attractions");
            for (int idx = 0; idx < attractionsJson.length(); idx++) {
                if (idx > 1) {
                    stringBuilder.append("|");
                }
                stringBuilder.append(attractionsJson.getJSONObject(idx).getString("name"));

            }
            eventTabInfo.setArtistName(stringBuilder.toString());
            JSONObject venueJson = EventDetailsJson.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            eventTabInfo.setVenueName(venueJson.getString("name"));

            JSONObject dateObject = EventDetailsJson.getJSONObject("dates").getJSONObject("start");
            eventTabInfo.setEventName(dateObject.getString("localDate") + " " + dateObject.getString("localTime"));
            JSONObject categoryJson = EventDetailsJson.getJSONArray("classifications").getJSONObject(0);
            eventTabInfo.setCategory(categoryJson.getJSONObject("segment").getString("name") + " | " + categoryJson.getJSONObject("genre").getString("name"));
            eventTabInfo.setTicketStatus(EventDetailsJson.getJSONObject("dates").getJSONObject("status").getString("code"));
            eventTabInfo.setBuyTicketUrl(EventDetailsJson.getString("url"));
            eventTabInfo.setSeatMapUrl(EventDetailsJson.getJSONObject("seatmap").getString("staticUrl"));

            // Building Venue Object
            venueTabInfo.setVenueName(eventTabInfo.getVenueName());
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

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
