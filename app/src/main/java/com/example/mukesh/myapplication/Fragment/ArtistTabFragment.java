package com.example.mukesh.myapplication.Fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mukesh.myapplication.ArtistTabAdaptor;
import com.example.mukesh.myapplication.POJO.ArtistTabAdaptorPojo;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.SpotifyInfo;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ArtistTabFragment extends Fragment {
    public static RecyclerView recyclerView;
    public static RecyclerView.LayoutManager layoutManager;
    public static ArtistTabAdaptor adaptor;
    public static Context applicationCtx;
    public View view;
    List<ArtistTabAdaptorPojo> artistTabAdaptorPojoList = new ArrayList<>();


    public ArtistTabFragment() {
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
        view = inflater.inflate(R.layout.fragment_artist_tab, container, false);

        String artistInfoStr = getArguments().getString("artistInfo");
        String artistImagesStr = getArguments().getString("artistImages");

        if (Objects.isNull(artistImagesStr)) {
            return view;
        }
        applicationCtx = view.getContext();
        List<SpotifyInfo> artistInfo = new Gson().fromJson(artistInfoStr, new TypeToken<List<SpotifyInfo>>() {
        }.getType());

        recyclerView = view.findViewById(R.id.artistItemList);
        layoutManager = new LinearLayoutManager(applicationCtx);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        artistTabAdaptorPojoList = new ArrayList<>();

        try {

            JSONArray jsonArray = new JSONArray(artistImagesStr);
            for (int idx = 0; idx < jsonArray.length(); idx++) {
                ArtistTabAdaptorPojo artistTabAdaptorPojo = new ArtistTabAdaptorPojo();
                List<String> images = new ArrayList<>();
                JSONArray artistImagesListJson = jsonArray.getJSONObject(idx).getJSONArray("artistImagesList");
                for (int imgIdx = 0; imgIdx < artistImagesListJson.length(); imgIdx++) {
                    images.add(artistImagesListJson.getString(imgIdx));
                }

                artistTabAdaptorPojo.setImages(images);

                String artistName = jsonArray.getJSONObject(idx).optString("artistName");
                artistTabAdaptorPojo.setHeading(artistName);

                if (Objects.nonNull(artistInfo) && artistInfo.size() > idx) {
                    artistTabAdaptorPojo.setFollowers(artistInfo.get(idx).getFollowers());
                    artistTabAdaptorPojo.setPopularity(artistInfo.get(idx).getPopularity());
                    artistTabAdaptorPojo.setCheckAtUrl(artistInfo.get(idx).getSpotifyUrl());
                }

                artistTabAdaptorPojoList.add(artistTabAdaptorPojo);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adaptor = new ArtistTabAdaptor(artistTabAdaptorPojoList, applicationCtx);
        recyclerView.setAdapter(adaptor);
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

    }
}



