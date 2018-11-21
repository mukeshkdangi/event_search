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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mukesh.myapplication.POJO.ArtistImageInfo;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistTabFragment extends Fragment {
    public GetArtistTabDetails getArtistTabDetails;

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
        View view = inflater.inflate(R.layout.artist_tab_items, container, false);

        String artistInfoStr = getArguments().getString("artistInfo");
        String artistImagesStr = getArguments().getString("artistImages");

        if (Objects.isNull(artistImagesStr)) {
            return view;
        }

        List<SpotifyInfo> artistInfo = new Gson().fromJson(artistInfoStr, new TypeToken<List<SpotifyInfo>>() {
        }.getType());
        List<ArtistImageInfo> artistImages = new Gson().fromJson(artistImagesStr, new TypeToken<List<ArtistImageInfo>>() {
        }.getType());

        try {
            JSONArray jsonArray = new JSONArray(artistImagesStr);
            for (int idx = 0; idx < jsonArray.length(); idx++) {
                List<String> images = new ArrayList<>();
                JSONArray artistImagesListJson = jsonArray.getJSONObject(idx).getJSONArray("artistImagesList");
                for (int imgIdx = 0; imgIdx < artistImagesListJson.length(); imgIdx++) {
                    images.add(artistImagesListJson.getString(imgIdx));
                }
                String artistName = jsonArray.getJSONObject(idx).optString("artistName");
                if (idx == 0) {
                    TextView textView = view.findViewById(R.id.artist_heading_1);
                    textView.setText(artistName);

                    if (Objects.nonNull(artistInfo) && artistInfo.size() > 0) {
                        textView = view.findViewById(R.id.name_row_value_1);
                        textView.setText(artistName);

                        textView = view.findViewById(R.id.follower_row_value_1);
                        textView.setText(artistInfo.get(idx).getFollowers());

                        textView = view.findViewById(R.id.popularity_row_value_1);
                        textView.setText(artistInfo.get(idx).getPopularity());

                        textView = view.findViewById(R.id.checkat_row_key_value_1);
                        textView.setText(artistInfo.get(idx).getSpotifyUrl());
                    } else {
                        view.findViewById(R.id.name_row_1).setVisibility(View.GONE);
                        view.findViewById(R.id.follower_row_1).setVisibility(View.GONE);
                        view.findViewById(R.id.popularity_row_1).setVisibility(View.GONE);
                        view.findViewById(R.id.checkat_row_1).setVisibility(View.GONE);

                    }
                    if (images.size() > 0) {
                        ImageView imageView = view.findViewById(R.id.image_1_1);
                        Glide.with(view.getContext()).load(images.get(0)).into(imageView);

                        if (images.size() > 1) {
                            imageView = view.findViewById(R.id.image_1_2);
                            Glide.with(view.getContext()).load(images.get(1)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_12).setVisibility(View.GONE);
                        }
                        if (images.size() > 2) {
                            imageView = view.findViewById(R.id.image_1_3);
                            Glide.with(view.getContext()).load(images.get(2)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_13).setVisibility(View.GONE);
                        }
                        if (images.size() > 3) {
                            imageView = view.findViewById(R.id.image_1_4);
                            Glide.with(view.getContext()).load(images.get(3)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_14).setVisibility(View.GONE);
                        }
                        if (images.size() > 4) {
                            imageView = view.findViewById(R.id.image_1_5);
                            Glide.with(view.getContext()).load(images.get(4)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_15).setVisibility(View.GONE);
                        }
                        if (images.size() > 5) {
                            imageView = view.findViewById(R.id.image_1_6);
                            Glide.with(view.getContext()).load(images.get(5)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_16).setVisibility(View.GONE);
                        }
                        if (images.size() > 6) {
                            imageView = view.findViewById(R.id.image_1_7);
                            Glide.with(view.getContext()).load(images.get(6)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_17).setVisibility(View.GONE);
                        }
                        if (images.size() > 7) {
                            imageView = view.findViewById(R.id.image_1_8);
                            Glide.with(view.getContext()).load(images.get(7)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_18).setVisibility(View.GONE);
                        }
                        if (images.size() > 8) {
                            imageView = view.findViewById(R.id.image_1_9);
                            Glide.with(view.getContext()).load(images.get(7)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_19).setVisibility(View.GONE);
                        }

                    } else {
                        view.findViewById(R.id.image_11).setVisibility(View.GONE);
                        view.findViewById(R.id.image_12).setVisibility(View.GONE);
                        view.findViewById(R.id.image_13).setVisibility(View.GONE);
                        view.findViewById(R.id.image_14).setVisibility(View.GONE);
                        view.findViewById(R.id.image_15).setVisibility(View.GONE);
                        view.findViewById(R.id.image_16).setVisibility(View.GONE);
                        view.findViewById(R.id.image_17).setVisibility(View.GONE);
                        view.findViewById(R.id.image_18).setVisibility(View.GONE);
                        view.findViewById(R.id.image_19).setVisibility(View.GONE);

                    }

                }
                if (jsonArray.length() == 1) {
                    view.findViewById(R.id.name_row_2).setVisibility(View.GONE);
                    view.findViewById(R.id.follower_row_2).setVisibility(View.GONE);
                    view.findViewById(R.id.popularity_row_2).setVisibility(View.GONE);
                    view.findViewById(R.id.checkat_row_2).setVisibility(View.GONE);
                    view.findViewById(R.id.image_21).setVisibility(View.GONE);
                    view.findViewById(R.id.image_22).setVisibility(View.GONE);
                    view.findViewById(R.id.image_23).setVisibility(View.GONE);
                    view.findViewById(R.id.image_24).setVisibility(View.GONE);
                    view.findViewById(R.id.image_25).setVisibility(View.GONE);
                    view.findViewById(R.id.image_26).setVisibility(View.GONE);
                    view.findViewById(R.id.image_27).setVisibility(View.GONE);
                    view.findViewById(R.id.image_28).setVisibility(View.GONE);
                    view.findViewById(R.id.image_29).setVisibility(View.GONE);

                }
                if (idx == 1) {
                    TextView textView = view.findViewById(R.id.artist_heading_2);
                    textView.setText(artistName);


                    if (Objects.nonNull(artistInfo) && artistInfo.size() > 1) {
                        textView = view.findViewById(R.id.name_row_value_2);
                        textView.setText(artistName);

                        textView = view.findViewById(R.id.follower_row_value_2);
                        textView.setText(artistInfo.get(idx).getFollowers());

                        textView = view.findViewById(R.id.popularity_row_value_2);
                        textView.setText(artistInfo.get(idx).getPopularity());

                        textView = view.findViewById(R.id.checkat_row_key_value_2);
                        textView.setText(artistInfo.get(idx).getSpotifyUrl());
                    } else {
                        view.findViewById(R.id.name_row_2).setVisibility(View.GONE);
                        view.findViewById(R.id.follower_row_2).setVisibility(View.GONE);
                        view.findViewById(R.id.popularity_row_2).setVisibility(View.GONE);
                        view.findViewById(R.id.checkat_row_2).setVisibility(View.GONE);
                    }

                    if (images.size() > 0) {
                        ImageView imageView = view.findViewById(R.id.image_2_1);
                        Glide.with(view.getContext()).load(images.get(0)).into(imageView);

                        if (images.size() > 1) {
                            imageView = view.findViewById(R.id.image_2_2);
                            Glide.with(view.getContext()).load(images.get(1)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_22).setVisibility(View.GONE);
                        }
                        if (images.size() > 2) {
                            imageView = view.findViewById(R.id.image_2_3);
                            Glide.with(view.getContext()).load(images.get(2)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_23).setVisibility(View.GONE);
                        }
                        if (images.size() > 3) {
                            imageView = view.findViewById(R.id.image_2_4);
                            Glide.with(view.getContext()).load(images.get(3)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_24).setVisibility(View.GONE);
                        }
                        if (images.size() > 4) {
                            imageView = view.findViewById(R.id.image_2_5);
                            Glide.with(view.getContext()).load(images.get(4)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_25).setVisibility(View.GONE);
                        }
                        if (images.size() > 5) {
                            imageView = view.findViewById(R.id.image_2_6);
                            Glide.with(view.getContext()).load(images.get(5)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_26).setVisibility(View.GONE);
                        }
                        if (images.size() > 6) {
                            imageView = view.findViewById(R.id.image_2_7);
                            Glide.with(view.getContext()).load(images.get(6)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_27).setVisibility(View.GONE);
                        }
                        if (images.size() > 7) {
                            imageView = view.findViewById(R.id.image_2_8);
                            Glide.with(view.getContext()).load(images.get(7)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_28).setVisibility(View.GONE);
                        }
                        if (images.size() > 8) {
                            imageView = view.findViewById(R.id.image_2_9);
                            Glide.with(view.getContext()).load(images.get(7)).into(imageView);
                        } else {
                            view.findViewById(R.id.image_29).setVisibility(View.GONE);
                        }

                    } else {
                        view.findViewById(R.id.image_21).setVisibility(View.GONE);
                        view.findViewById(R.id.image_22).setVisibility(View.GONE);
                        view.findViewById(R.id.image_23).setVisibility(View.GONE);
                        view.findViewById(R.id.image_24).setVisibility(View.GONE);
                        view.findViewById(R.id.image_25).setVisibility(View.GONE);
                        view.findViewById(R.id.image_26).setVisibility(View.GONE);
                        view.findViewById(R.id.image_27).setVisibility(View.GONE);
                        view.findViewById(R.id.image_28).setVisibility(View.GONE);
                        view.findViewById(R.id.image_29).setVisibility(View.GONE);

                    }
                }

            }
        } catch (Exception e) {
            view.findViewById(R.id.image_11).setVisibility(View.GONE);
            view.findViewById(R.id.image_12).setVisibility(View.GONE);
            view.findViewById(R.id.image_13).setVisibility(View.GONE);
            view.findViewById(R.id.image_14).setVisibility(View.GONE);
            view.findViewById(R.id.image_15).setVisibility(View.GONE);
            view.findViewById(R.id.image_16).setVisibility(View.GONE);
            view.findViewById(R.id.image_17).setVisibility(View.GONE);
            view.findViewById(R.id.image_18).setVisibility(View.GONE);
            view.findViewById(R.id.image_19).setVisibility(View.GONE);
            view.findViewById(R.id.image_21).setVisibility(View.GONE);
            view.findViewById(R.id.image_22).setVisibility(View.GONE);
            view.findViewById(R.id.image_23).setVisibility(View.GONE);
            view.findViewById(R.id.image_24).setVisibility(View.GONE);
            view.findViewById(R.id.image_25).setVisibility(View.GONE);
            view.findViewById(R.id.image_26).setVisibility(View.GONE);
            view.findViewById(R.id.image_27).setVisibility(View.GONE);
            view.findViewById(R.id.image_28).setVisibility(View.GONE);
            view.findViewById(R.id.image_29).setVisibility(View.GONE);
            view.findViewById(R.id.name_row_2).setVisibility(View.GONE);
            view.findViewById(R.id.follower_row_2).setVisibility(View.GONE);
            view.findViewById(R.id.popularity_row_2).setVisibility(View.GONE);
            view.findViewById(R.id.checkat_row_2).setVisibility(View.GONE);
            view.findViewById(R.id.name_row_1).setVisibility(View.GONE);
            view.findViewById(R.id.follower_row_1).setVisibility(View.GONE);
            view.findViewById(R.id.popularity_row_1).setVisibility(View.GONE);
            view.findViewById(R.id.checkat_row_1).setVisibility(View.GONE);

        }


        Log.i("artistInfo", new Gson().toJson(artistInfo));
        Log.i("artistImages", new Gson().toJson(artistImages));
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



