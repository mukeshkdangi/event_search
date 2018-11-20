package com.example.mukesh.myapplication.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.mukesh.myapplication.Fragment.ArtistTabFragment;
import com.example.mukesh.myapplication.Fragment.EventTabFragment;
import com.example.mukesh.myapplication.Fragment.UpcomingEventsFragment;
import com.example.mukesh.myapplication.Fragment.VenueTabFragment;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.EventTabInfo;
import com.example.mukesh.myapplication.POJO.SpotifyInfo;
import com.example.mukesh.myapplication.POJO.UpcomingEventInfo;
import com.example.mukesh.myapplication.POJO.VenueTabInfo;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.ViewPagerAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventMoreDetails extends AppCompatActivity {

    static Toolbar toolbar;
    static TabLayout tabLayout;
    static ViewPager viewPager;
    static ViewPagerAdapter viewPagerAdapter;
    static FragmentManager fragMaanger;
    static Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_more_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fragMaanger = getSupportFragmentManager();

        EventMoreDetails.viewPagerAdapter = new ViewPagerAdapter(EventMoreDetails.fragMaanger, EventMoreDetails.bundle);
        EventMoreDetails.viewPagerAdapter.addFragment(new EventTabFragment(), "EVENT");
        EventMoreDetails.viewPagerAdapter.addFragment(new ArtistTabFragment(), "ARTIST(S)");
        EventMoreDetails.viewPagerAdapter.addFragment(new VenueTabFragment(), "VENUE");
        EventMoreDetails.viewPagerAdapter.addFragment(new UpcomingEventsFragment(), "UPCOMING");
        EventMoreDetails.viewPager.setAdapter(EventMoreDetails.viewPagerAdapter);
        EventMoreDetails.tabLayout.setupWithViewPager(EventMoreDetails.viewPager);
        setupTabIcons();

        new GetEventTabDetails().execute(getIntent().getStringExtra("eventDetails"));
        new GetArtistTabDetails().execute(getIntent().getStringExtra("eventDetails"));
        new GetVenueTabDetails().execute(getIntent().getStringExtra("eventDetails"));
        new GetUpcomingTabDetails().execute(getIntent().getStringExtra("eventDetails"));

    }

    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.artist,
            R.drawable.venue,
            R.drawable.upcoming
    };

    private void setupTabIcons() {
        EventMoreDetails.tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        EventMoreDetails.tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        EventMoreDetails.tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        EventMoreDetails.tabLayout.getTabAt(3).setIcon(tabIcons[3]);
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


        EventMoreDetails.bundle.putString("eventDetails", new Gson().toJson(eventDetail));
        EventMoreDetails.bundle.putString("eventTabInfo", new Gson().toJson(eventTabInfo));

    }


}


class GetArtistTabDetails extends AsyncTask<String, Integer, String> {

    EventDetails eventDetail;
    List<SpotifyInfo> artistInfo;
    List<List<String>> artistImages;

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


        ///

        {
            try {
                JSONObject EventDetailsJson = new JSONObject(eventTabResultBuilder.toString());
                String eventName = EventDetailsJson.getString("name");
                List<String> attractionList = new ArrayList<>();

                JSONArray attractionsJson = EventDetailsJson.getJSONObject("_embedded").optJSONArray("attractions");
                if (Objects.nonNull(attractionsJson)) {
                    for (int idx = 0; idx < attractionsJson.length(); idx++) {
                        String name = attractionsJson.getJSONObject(idx).getString("name");
                        attractionList.add(name);
                    }
                }
                JSONObject categoryJson = EventDetailsJson.getJSONArray("classifications").getJSONObject(0);
                String categoryName = categoryJson.getJSONObject("segment").getString("name");
                artistImages = new ArrayList<>();

                for (int idx = 0; idx < attractionList.size(); idx++) {
                    urlBuilder = new StringBuilder();
                    urlBuilder.append("http://100.26.198.168:3000/api/google/getimages/");
                    urlBuilder.append(attractionList.get(idx));

                    eventTabResultBuilder = new StringBuilder();
                    Log.i("Hitting Url  for Photo tab result... ", urlBuilder.toString());
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

                        JSONObject imageJson = new JSONObject(eventTabResultBuilder.toString());

                        List<String> images = new ArrayList<>();
                        JSONArray imageJsonArray = imageJson.getJSONArray("items");

                        for (int img = 0; img < imageJsonArray.length(); img++) {
                            images.add(imageJsonArray.getJSONObject(img).getString("link"));
                        }
                        artistImages.add(images);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                // HIT Spotify

                artistInfo = new ArrayList<>();

                if (categoryName.contains("Music")) {
                    try {
                        for (int idx = 0; idx < attractionList.size(); idx++) {
                            SpotifyInfo spotifyInfo = new SpotifyInfo();

                            urlBuilder = new StringBuilder();
                            urlBuilder.append("http://100.26.198.168:3000/api/spotify/getartistbyname/");
                            urlBuilder.append(attractionList.get(idx));

                            eventTabResultBuilder = new StringBuilder();
                            Log.i("Hitting Url  for Spotify  result... ", urlBuilder.toString());

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

                            JSONObject spotifyDetails = new JSONObject(eventTabResultBuilder.toString());
                            JSONArray artistJsonArray = spotifyDetails.optJSONObject("artists").getJSONArray("items");

                            for (int art = 0; art < artistJsonArray.length(); art++) {
                                JSONObject jsonObject = artistJsonArray.getJSONObject(art);

                                if (attractionList.get(idx).equalsIgnoreCase(jsonObject.getString("name"))) {
                                    spotifyInfo.setArtistName(jsonObject.getString("name"));
                                    spotifyInfo.setFollowers(jsonObject.getJSONObject("followers").getString("total"));
                                    spotifyInfo.setSpotifyUrl(jsonObject.getJSONObject("external_urls").getString("spotify"));
                                    spotifyInfo.setPopularity(jsonObject.getString("popularity"));
                                    break;
                                }
                            }
                            artistInfo.add(spotifyInfo);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return eventTabResultBuilder.toString();

    }

    @Override
    protected void onPostExecute(String eventTabResults) {
        EventMoreDetails.bundle.putString("artistInfo", new Gson().toJson(artistInfo));
        EventMoreDetails.bundle.putString("artistImages", new Gson().toJson(artistImages));

    }
}


class GetUpcomingTabDetails extends AsyncTask<String, Integer, String> {
    List<UpcomingEventInfo> upcomingEventInfos;

    @Override
    protected String doInBackground(String... strings) {

        EventDetails eventDetail = new Gson().fromJson(strings[0], EventDetails.class);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://100.26.198.168:3000/api/songkick/getvenueidbyvenuename/");
        urlBuilder.append(eventDetail.getEventVenue());

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


            JSONObject JSONObject = new JSONObject(eventTabResultBuilder.toString());
            String venueId = JSONObject.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("venue").getJSONObject(0).getString("id");


            StringBuilder songKickUrl = new StringBuilder();
            songKickUrl.append("http://100.26.198.168:3000/api/songkick/getvenuebyid/");
            songKickUrl.append(venueId);
            Log.i("SongKick for URL ", songKickUrl.toString());

            eventTabResultBuilder = new StringBuilder();

            URL songK = new URL(songKickUrl.toString());
            HttpURLConnection songKConnection = (HttpURLConnection) songK.openConnection();
            songKConnection.setRequestMethod("GET");
            songKConnection.setConnectTimeout(6000);
            songKConnection.setReadTimeout(6000);
            songKConnection.connect();
            BufferedReader songKrd = new BufferedReader(new InputStreamReader(songKConnection.getInputStream()));
            String responseLine;
            while ((responseLine = songKrd.readLine()) != null) {
                eventTabResultBuilder.append(responseLine);
            }

            songKrd.close();
            songKConnection.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventTabResultBuilder.toString();
    }

    @Override
    protected void onPostExecute(String upComingEvents) {
        upcomingEventInfos = new ArrayList<>();
        try {

            JSONObject upcomingEventJson = new JSONObject(upComingEvents);
            JSONArray upcomingEventJsonArr = upcomingEventJson.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");

            for (int idx = 0; idx < upcomingEventJsonArr.length(); idx++) {
                UpcomingEventInfo upcomingEventInfo = new UpcomingEventInfo();
                String displayName = upcomingEventJsonArr.getJSONObject(idx).optString("displayName");
                String url = upcomingEventJsonArr.getJSONObject(idx).optString("uri");
                JSONArray performance = upcomingEventJsonArr.getJSONObject(idx).getJSONArray("performance");
                String artistName = "";

                if (performance.length() > 0) {
                    artistName = performance.getJSONObject(0).optString("displayName");
                }

                String date = upcomingEventJsonArr.getJSONObject(idx).getJSONObject("start").getString("datetime");
                upcomingEventInfo.setArtistName(artistName);
                upcomingEventInfo.setDate(date);
                upcomingEventInfo.setEventName(displayName);
                upcomingEventInfo.setUrl(url);
                upcomingEventInfos.add(upcomingEventInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("upcomingEventInfos size", String.valueOf(upcomingEventInfos.size()));
        EventMoreDetails.bundle.putString("upcomingEventInfos", new Gson().toJson(upcomingEventInfos));

        EventMoreDetails.viewPagerAdapter = new ViewPagerAdapter(EventMoreDetails.fragMaanger, EventMoreDetails.bundle);
        EventMoreDetails.viewPagerAdapter.addFragment(new EventTabFragment(), "EVENT");
        EventMoreDetails.viewPagerAdapter.addFragment(new ArtistTabFragment(), "ARTIST(S)");
        EventMoreDetails.viewPagerAdapter.addFragment(new VenueTabFragment(), "VENUE");
        EventMoreDetails.viewPagerAdapter.addFragment(new UpcomingEventsFragment(), "UPCOMING");

        EventMoreDetails.viewPager.setOffscreenPageLimit(0);
        EventMoreDetails.viewPager.setAdapter(EventMoreDetails.viewPagerAdapter);
        EventMoreDetails.tabLayout.setupWithViewPager(EventMoreDetails.viewPager);
        setupTabIcons();
    }

    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.artist,
            R.drawable.venue,
            R.drawable.upcoming
    };

    private void setupTabIcons() {
        EventMoreDetails.tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        EventMoreDetails.tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        EventMoreDetails.tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        EventMoreDetails.tabLayout.getTabAt(3).setIcon(tabIcons[3]);
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

        EventMoreDetails.bundle.putString("venueTabInfo", new Gson().toJson(venueTabInfo));


    }
}



