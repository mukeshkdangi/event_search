package com.example.mukesh.myapplication.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.POJO.UpcomingEventInfo;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.UpComingEventTabAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class UpcomingEventsFragment extends Fragment {

    public GetUpcomingTabDetails getUpcomingTabDetails;
    public View view;
    FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public UpcomingEventsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.upcomging_event_parent_view, container, false);

        String upcomingEventInfosStr = getArguments().getString("upcomingEventInfos");

        if (Objects.isNull(upcomingEventInfosStr)) {
            return view;
        }

        final List<UpcomingEventInfo> upcomingEventInfos = new Gson().fromJson(upcomingEventInfosStr, new TypeToken<List<UpcomingEventInfo>>() {
        }.getType());

        Log.i("upcomingEventInfosStr", upcomingEventInfosStr);

        Spinner spinner = view.findViewById(R.id.order_spinner);

        List<String> categories = new ArrayList<>();
        categories.add("Default");
        categories.add("Event Name");
        categories.add("Time");
        categories.add("Artist");
        categories.add("Type");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        Spinner spinnerOrderType = view.findViewById(R.id.order_type_spinner);

        List<String> OrderType = new ArrayList<>();
        OrderType.add("Ascending");
        OrderType.add("Descending");

        ArrayAdapter<String> OrderTypedataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, OrderType);
        OrderTypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderType.setAdapter(OrderTypedataAdapter);


        RecyclerView recyclerView = view.findViewById(R.id.upcomingEventItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new UpComingEventTabAdapter(upcomingEventInfos));

        Collections.reverse(upcomingEventInfos);
        spinnerOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                Collections.reverse(upcomingEventInfos);
                RecyclerView recyclerView = view.findViewById(R.id.upcomingEventItemList);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new UpComingEventTabAdapter(upcomingEventInfos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {

                    case 1:
                        Collections.sort(upcomingEventInfos, new Comparator<UpcomingEventInfo>() {

                            public int compare(UpcomingEventInfo o1, UpcomingEventInfo o2) {
                                return o1.getEventName().compareTo(o2.getEventName());
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(upcomingEventInfos, new Comparator<UpcomingEventInfo>() {

                            public int compare(UpcomingEventInfo o1, UpcomingEventInfo o2) {
                                return o1.getDate().compareTo(o2.getDate());
                            }
                        });
                        break;

                    case 0:
                        Collections.sort(upcomingEventInfos, new Comparator<UpcomingEventInfo>() {

                            public int compare(UpcomingEventInfo o1, UpcomingEventInfo o2) {
                                return o1.getDate().compareTo(o2.getDate());
                            }
                        });
                        break;

                    case 4:
                        Collections.sort(upcomingEventInfos, new Comparator<UpcomingEventInfo>() {

                            public int compare(UpcomingEventInfo o1, UpcomingEventInfo o2) {
                                return o1.getType().compareTo(o2.getType());
                            }
                        });
                        break;

                    case 3:
                        Collections.sort(upcomingEventInfos, new Comparator<UpcomingEventInfo>() {

                            public int compare(UpcomingEventInfo o1, UpcomingEventInfo o2) {
                                return o1.getArtistName().compareTo(o2.getArtistName());
                            }
                        });
                        break;
                }

                RecyclerView recyclerView = view.findViewById(R.id.upcomingEventItemList);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new UpComingEventTabAdapter(upcomingEventInfos));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
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
    }
}
