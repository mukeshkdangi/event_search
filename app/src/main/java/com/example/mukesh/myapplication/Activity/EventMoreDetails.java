package com.example.mukesh.myapplication.Activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mukesh.myapplication.Fragment.ArtistTabFragment;
import com.example.mukesh.myapplication.Fragment.EventTabFragment;
import com.example.mukesh.myapplication.Fragment.UpcomingEventsFragment;
import com.example.mukesh.myapplication.Fragment.VenueTabFragment;
import com.example.mukesh.myapplication.POJO.ArtistImageInfo;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.EventTabInfo;
import com.example.mukesh.myapplication.POJO.SpotifyInfo;
import com.example.mukesh.myapplication.POJO.UpcomingEventInfo;
import com.example.mukesh.myapplication.POJO.VenueTabInfo;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.Storage.SharedPreferenceConfig;
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
    SharedPreferenceConfig sharedPreferenceConfig;
    EventDetails eventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_more_details);

        sharedPreferenceConfig = new SharedPreferenceConfig(this);

        eventDetails = new Gson().fromJson(getIntent().getStringExtra("eventDetails"), EventDetails.class);

        toolbar = findViewById(R.id.event_tab_toolbar);
        toolbar.setTitle(eventDetails.getEventName());

        final ImageView favIcon = findViewById(R.id.favicon2);
        favIcon.setImageResource(eventDetails.isFav() ? R.drawable.heart_fill_red : R.drawable.heart_fill_white);
        ImageView twitter = findViewById(R.id.twitter);
        twitter.setImageResource(R.drawable.twitter_ic);

        favIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addEventToFav(favIcon, view);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder twit = new StringBuilder();
                twit.append("Check out ").append(eventDetails.getEventName());
                twit.append(" located at ").append(eventDetails.getEventVenue());
                twit.append(" website : ").append(eventDetails.getUrl());

                Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
                String url = "http://www.twitter.com/intent/tweet?text=" + twit.toString();

                sharingIntent.setData(Uri.parse(url));
                Intent chooserIntent = Intent.createChooser(sharingIntent, "Open in...");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{sharingIntent});
                startActivity(chooserIntent);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fragMaanger = getSupportFragmentManager();

        bundle.clear();
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
       // new GetVenueTabDetails().execute(getIntent().getStringExtra("eventDetails"));
        new GetUpcomingTabDetails().execute(getIntent().getStringExtra("eventDetails"));

    }

    private void addEventToFav(ImageView img, View view) {
        try {

            if (eventDetails.isFav()) {
                eventDetails.setFav(false);
                img.setImageResource(R.drawable.heart_fill_white);
                sharedPreferenceConfig.removeFromSharedPref(new Gson().toJson(eventDetails));
                Toast toast = Toast.makeText(this, eventDetails.getEventName() + "was removed to favorites",
                        Toast.LENGTH_LONG);
                TextView text = toast.getView().findViewById(android.R.id.message);
                text.setBackgroundResource(R.drawable.dialog_bg);
                toast.show();
                img.setTag(new Gson().toJson(eventDetails));
                return;
            }


            img.setImageResource(R.drawable.heart_fill_red);
            eventDetails.setFav(true);

            sharedPreferenceConfig.saveSharedPreferencesLogList(new Gson().toJson(eventDetails));
            Toast toast = Toast.makeText(this, eventDetails.getEventName() + "was added from favorites",
                    Toast.LENGTH_LONG);
            TextView text = toast.getView().findViewById(android.R.id.message);
            text.setBackgroundColor(PorterDuff.Mode.SRC.ordinal());
            toast.show();
            img.setTag(new Gson().toJson(eventDetails));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
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
                    if (idx > 0) {
                        stringBuilder.append(" | ");
                    }
                    stringBuilder.append(attractionsJson.getJSONObject(idx).getString("name"));

                }
            }
            eventTabInfo.setArtistName(stringBuilder.toString());
            JSONObject venueJson = EventDetailsJson.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            eventTabInfo.setVenueName(venueJson.getString("name"));

            JSONObject dateObject = EventDetailsJson.getJSONObject("dates").getJSONObject("start");
            eventTabInfo.setTime(dateObject.getString("localDate") + "  " + dateObject.getString("localTime"));
            JSONObject categoryJson = EventDetailsJson.getJSONArray("classifications").getJSONObject(0);
            eventTabInfo.setCategory(categoryJson.getJSONObject("segment").getString("name") + " | " + categoryJson.getJSONObject("genre").getString("name"));
            eventTabInfo.setTicketStatus(EventDetailsJson.getJSONObject("dates").getJSONObject("status").getString("code"));
            eventTabInfo.setBuyTicketUrl(EventDetailsJson.getString("url"));
            if (EventDetailsJson.optJSONObject("seatmap") != null && Objects.nonNull(EventDetailsJson.getJSONObject("seatmap").getString("staticUrl")))
                eventTabInfo.setSeatMapUrl(EventDetailsJson.getJSONObject("seatmap").getString("staticUrl"));

            if (Objects.nonNull(EventDetailsJson.optJSONArray("priceRanges")) && (EventDetailsJson.optJSONArray("priceRanges").length() > 0)) {
                String minPrice = EventDetailsJson.optJSONArray("priceRanges").getJSONObject(0).optString("min");
                String maxPrice = EventDetailsJson.optJSONArray("priceRanges").getJSONObject(0).optString("max");
                eventTabInfo.setPriceRange("$" + minPrice + " ~ $" + maxPrice);
            }
            // Building Venue Object
            try {
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
                venueTabInfo.setLon(venueJson.getJSONObject("location").getString("longitude"));
                venueTabInfo.setLat(venueJson.getJSONObject("location").getString("latitude"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        EventMoreDetails.bundle.putString("eventDetails", new Gson().toJson(eventDetail));
        EventMoreDetails.bundle.putString("eventTabInfo", new Gson().toJson(eventTabInfo));
        EventMoreDetails.bundle.putString("venueTabInfo", new Gson().toJson(venueTabInfo));

    }


}


class GetArtistTabDetails extends AsyncTask<String, Integer, String> {

    EventDetails eventDetail;
    List<SpotifyInfo> artistInfo;
    List<ArtistImageInfo> artistImageInfoList;

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
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
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


        {
            try {
                Log.i("eventTabResultBuilder.toString()", eventTabResultBuilder.toString());
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
                artistImageInfoList = new ArrayList<>();

                for (int idx = 0; idx < attractionList.size(); idx++) {
                    ArtistImageInfo artistImageInfo = new ArtistImageInfo();
                    artistImageInfo.setArtistName(attractionList.get(idx));

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
                        artistImageInfo.setArtistImagesList(images);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    artistImageInfoList.add(artistImageInfo);
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
        EventMoreDetails.bundle.putString("artistImages", new Gson().toJson(artistImageInfoList));

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
            int minFive = Math.min(upcomingEventJsonArr.length(), 5);

            for (int idx = 0; idx < minFive; idx++) {
                UpcomingEventInfo upcomingEventInfo = new UpcomingEventInfo();
                String displayName = upcomingEventJsonArr.getJSONObject(idx).optString("displayName");
                String url = upcomingEventJsonArr.getJSONObject(idx).optString("uri");
                upcomingEventInfo.setId(Integer.valueOf(upcomingEventJsonArr.getJSONObject(idx).optString("id")));
                JSONArray performance = upcomingEventJsonArr.getJSONObject(idx).getJSONArray("performance");
                String artistName = "";

                if (performance.length() > 0) {
                    artistName = performance.getJSONObject(0).optString("displayName");
                }
                String date = "";
                if (upcomingEventJsonArr.getJSONObject(idx).getJSONObject("start") != null) {
                    date = upcomingEventJsonArr.getJSONObject(idx).getJSONObject("start").getString("date");
                    String timeStr = upcomingEventJsonArr.getJSONObject(idx).getJSONObject("start").optString("time");
                    if (Objects.nonNull(timeStr) && !timeStr.equalsIgnoreCase("null")) {
                        date = date + " " + timeStr;
                    }
                }
                upcomingEventInfo.setArtistName(artistName);
                upcomingEventInfo.setDate(date);
                upcomingEventInfo.setEventName(displayName);
                upcomingEventInfo.setUrl(url);
                upcomingEventInfo.setType("Type :" + upcomingEventJsonArr.getJSONObject(idx).optString("type"));
                upcomingEventInfos.add(upcomingEventInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
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
            venueTabInfo.setLon(venueJson.getJSONObject("location").getString("longitude"));
            venueTabInfo.setLat(venueJson.getJSONObject("location").getString("latitude"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        EventMoreDetails.bundle.putString("venueTabInfo", new Gson().toJson(venueTabInfo));


    }
}



