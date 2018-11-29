package com.example.mukesh.myapplication.Fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import com.example.mukesh.myapplication.EventTabAdapter;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.EventTabDataList;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventTabFragment extends Fragment {

    public View view;
    public Context appContext;
    public FragmentManager fragmentTransaction;
    public EventTabFragment eventTabFragment;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    public EventTabFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String eventTabInfoStr = getArguments().getString("eventTabInfo");
        Log.i("eventDetail", new Gson().toJson(eventTabInfoStr));
        view = inflater.inflate(R.layout.fragment_event_tab, container, false);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        progressBarHolder.dispatchDisplayHint(View.VISIBLE);

        EventTabInfo eventTabInfo = new Gson().fromJson(eventTabInfoStr, EventTabInfo.class);

        if (Objects.isNull(eventTabInfo)) {
            // progressDialog.show(getChildFragmentManager(), "In Progress.....");
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            return view;

        }
        // progressDialog.cancel();
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.eventItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        List<EventTabDataList> eventTabDataListList = new ArrayList<>();
        EventTabDataList eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Artist/Team(s)");
        eventTabDataList.setValue(eventTabInfo.getArtistName());
        eventTabDataListList.add(eventTabDataList);

        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Venue");
        eventTabDataList.setValue(eventTabInfo.getVenueName());
        eventTabDataListList.add(eventTabDataList);


        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Time");

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        try {
            Date time = ft.parse(eventTabInfo.getTime());
            ft.applyPattern("MMM dd, yyyy hh:mm:ss");
            eventTabDataList.setValue(ft.format(time));
        } catch (ParseException e) {
            try {

                ft = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date time = ft.parse(eventTabInfo.getTime());
                ft.applyPattern("MMM dd, yyyy");
                eventTabDataList.setValue(ft.format(time));
            } catch (Exception e2) {
                eventTabDataList.setValue("-");
            }
        }

        eventTabDataListList.add(eventTabDataList);


        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Category");
        eventTabDataList.setValue(eventTabInfo.getCategory());
        eventTabDataListList.add(eventTabDataList);

        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Ticket Status");
        eventTabDataList.setValue(eventTabInfo.getTicketStatus());
        eventTabDataListList.add(eventTabDataList);

        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Price Range");
        eventTabDataList.setValue(eventTabInfo.getPriceRange());
        eventTabDataListList.add(eventTabDataList);


        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Buy Ticket at");
        eventTabDataList.setValue(eventTabInfo.getBuyTicketUrl());
        eventTabDataListList.add(eventTabDataList);


        eventTabDataList = new EventTabDataList();
        eventTabDataList.setKey("Seat Map");
        eventTabDataList.setValue(eventTabInfo.getSeatMapUrl());
        eventTabDataList.setSeatMapURL(true);
        eventTabDataListList.add(eventTabDataList);


        recyclerView.setAdapter(new EventTabAdapter(eventTabDataListList));

        fragmentTransaction = getFragmentManager();
        appContext = getActivity().getApplicationContext();
        eventTabFragment = EventTabFragment.this;


        Log.i("getEventTabDetails", new Gson().toJson(eventTabInfo));
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

        eventTabInfo = new EventTabInfo();
        venueTabInfo = new VenueTabInfo();

        try {
            JSONObject EventDetailsJson = new JSONObject(eventTabResults);
            eventTabInfo.setEventName(EventDetailsJson.getString("name"));
            StringBuilder stringBuilder = new StringBuilder();
            JSONArray attractionsJson = EventDetailsJson.getJSONObject("_embedded").optJSONArray("attractions");
            if (Objects.nonNull(attractionsJson)) {
                for (int idx = 0; idx < attractionsJson.length(); idx++) {
                    if (idx > 1) {
                        stringBuilder.append("|");
                    }
                    stringBuilder.append(attractionsJson.getJSONObject(idx).getString("name"));

                }
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
