package com.example.mukesh.myapplication.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.EventTabInfo;
import com.example.mukesh.myapplication.POJO.SpotifyInfo;
import com.example.mukesh.myapplication.POJO.VenueTabInfo;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistTabFragment extends Fragment {


    public ArtistTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_tab, container, false);
        String eventDetails = getArguments().getString("eventDetails");
        EventDetails eventDetail = new Gson().fromJson(eventDetails, EventDetails.class);
        new GetArtistTabDetails().execute(eventDetails);

        return view;

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
        return null;

    }

    @Override
    protected void onPostExecute(String eventTabResults) {
        Log.i("eventTabResults", eventTabResults);
        try {
            JSONObject EventDetailsJson = new JSONObject(eventTabResults);
            String eventName = EventDetailsJson.getString("name");
            List<String> attractionList = new ArrayList<>();

            JSONArray attractionsJson = EventDetailsJson.getJSONObject("_embedded").getJSONArray("attractions");
            for (int idx = 0; idx < attractionsJson.length(); idx++) {
                String name = attractionsJson.getJSONObject(idx).getString("name");
                attractionList.add(name);
            }
            JSONObject categoryJson = EventDetailsJson.getJSONArray("classifications").getJSONObject(0);
            String categoryName = categoryJson.getJSONObject("segment").getString("name");
            artistImages = new ArrayList<>();

            for (int idx = 0; idx < attractionList.size(); idx++) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("http://100.26.198.168:3000/api/google/getimages/");
                urlBuilder.append(attractionList.get(idx));

                StringBuilder eventTabResultBuilder = new StringBuilder();
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

                        StringBuilder urlBuilder = new StringBuilder();
                        urlBuilder.append("http://100.26.198.168:3000/api/spotify/getartistbyname/");
                        urlBuilder.append(attractionList.get(idx));

                        StringBuilder eventTabResultBuilder = new StringBuilder();
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
                        JSONArray artistJsonArray = spotifyDetails.getJSONArray("items");

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
}
