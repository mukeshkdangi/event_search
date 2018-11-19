package com.example.mukesh.myapplication.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpcomingEventsFragment extends Fragment {


    public UpcomingEventsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
        String eventDetails = getArguments().getString("eventDetails");
        EventDetails eventDetail = new Gson().fromJson(eventDetails, EventDetails.class);
        new GetUpcomingTabDetails().execute(eventDetails);

        return view;
    }


}

class GetUpcomingTabDetails extends AsyncTask<String, Integer, String> {

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

    }
}
