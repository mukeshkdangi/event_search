package com.example.mukesh.myapplication.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.VenueTabInfo;
import com.example.mukesh.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class VenueTabFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    View view;
    VenueTabInfo eventDetail;
    private static LatLng ONE = new LatLng(34.882216, -118.222028);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //  mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public VenueTabFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (this.googleMap == null) {
            this.googleMap = googleMap;
        }
        if (Objects.isNull(eventDetail)) return;

        if (Objects.nonNull(eventDetail.getLat()) && Objects.nonNull(eventDetail.getLon()))
            ONE = new LatLng(Float.parseFloat(eventDetail.getLat()), Float.parseFloat(eventDetail.getLon()));
        googleMap.addMarker(new MarkerOptions().position(ONE)
                .title(eventDetail.getVenueName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ONE));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(ONE);
        LatLngBounds bounds = builder.build();
        CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ONE, 14.0f));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_venue_tab, container, false);
        String venueTabInfoStr = getArguments().getString("venueTabInfo");

        eventDetail = new Gson().fromJson(venueTabInfoStr, VenueTabInfo.class);

        if (googleMap == null) {
            SupportMapFragment mapFrag = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.viewMap));
            mapFrag.getMapAsync(this);
        }


        if (Objects.isNull(venueTabInfoStr)) {
            return view;
        }


        Log.i("eventDetail", new Gson().toJson(eventDetail));
        TextView textView = view.findViewById(R.id.name_row_key_value);
        textView.setText(eventDetail.getVenueName());

        textView = view.findViewById(R.id.address_row_key_value);
        textView.setText(eventDetail.getAddress());

        textView = view.findViewById(R.id.city_row_key_value);
        textView.setText(eventDetail.getCity());

        textView = view.findViewById(R.id.ph_row_1_txt_value);
        textView.setText(eventDetail.getPhoneNumber());

        String text;


        WebView webView = view.findViewById(R.id.open_hours_row_1_value);
        text = "<html><body><p align=\"justify\">";
        text += eventDetail.getOpenHours();
        text += "</p></body></html>";
        webView.setVerticalScrollBarEnabled(false);
        webView.loadData(text, "text/html", "utf-8");

        webView = view.findViewById(R.id.general_rule_row_value);
        text = "<html><body><p align=\"justify\">";
        text += eventDetail.getGeneralRule();
        text += "</p></body></html>";
        webView.setVerticalScrollBarEnabled(false);
        webView.loadData(text, "text/html", "utf-8");

        webView = view.findViewById(R.id.child_rule_row_value);
        text = "<html><body><p align=\"justify\">";
        text += eventDetail.getChildRule();
        text += "</p></body></html>";
        webView.setVerticalScrollBarEnabled(false);
        webView.loadData(text, "text/html", "utf-8");

        if (Objects.isNull(eventDetail.getOpenHours()) || eventDetail.getOpenHours().equalsIgnoreCase("")) {
            view.findViewById(R.id.open_hours_row_1_value).setVisibility(View.GONE);
            view.findViewById(R.id.open_hours_row_1_txt).setVisibility(View.GONE);
        }
        if (Objects.isNull(eventDetail.getGeneralRule()) || eventDetail.getGeneralRule().equalsIgnoreCase("")) {
            view.findViewById(R.id.general_rule_row_txt).setVisibility(View.GONE);
            view.findViewById(R.id.general_rule_row_value).setVisibility(View.GONE);
        }
        if (Objects.isNull(eventDetail.getChildRule()) || eventDetail.getChildRule().equalsIgnoreCase("")) {
            view.findViewById(R.id.child_rule_row_txt).setVisibility(View.GONE);
            view.findViewById(R.id.child_rule_row_value).setVisibility(View.GONE);
        }
        if (Objects.isNull(eventDetail.getPhoneNumber()) || eventDetail.getChildRule().equalsIgnoreCase("")) {
            view.findViewById(R.id.ph_row_1_txt_value).setVisibility(View.GONE);
            view.findViewById(R.id.ph_row_1_txt_key).setVisibility(View.GONE);
        }


        return view;
    }

    private void setUpMap(GoogleMap map) {
        googleMap = map;
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
