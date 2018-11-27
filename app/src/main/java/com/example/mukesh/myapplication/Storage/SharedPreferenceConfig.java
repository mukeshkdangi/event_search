package com.example.mukesh.myapplication.Storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.mukesh.myapplication.POJO.EventDetails;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SharedPreferenceConfig {

    private SharedPreferences sharedPreferences;
    private Context context;


    public SharedPreferenceConfig(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public void saveSharedPreferencesLogList(String details) {

        SharedPreferences.Editor prefsEditor = this.sharedPreferences.edit();
        EventDetails eventDetails = new Gson().fromJson(details, EventDetails.class);
        eventDetails.setFav(true);
        details = new Gson().toJson(eventDetails);
        prefsEditor.putString(eventDetails.getEventId(), details);
        prefsEditor.commit();
        loadSharedPreferencesLogList();
    }

    public List<EventDetails> loadSharedPreferencesLogList() {
        List<EventDetails> savedCollage = new ArrayList<>();
        Map<String, ?> eventDetailMap = this.sharedPreferences.getAll();

        if (eventDetailMap == null || eventDetailMap.isEmpty()) {
            return savedCollage;
        } else {
            List<Object> savedCollages = eventDetailMap.values().stream()
                    .collect(Collectors.toList());
            for (Object collage : savedCollages) {
                savedCollage.add(new Gson().fromJson(collage.toString(), EventDetails.class));
            }
        }

        return savedCollage;
    }

    public void removeFromSharedPref(String details) {
        EventDetails eventDetails = new Gson().fromJson(details, EventDetails.class);
        eventDetails.setFav(false);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(eventDetails.getEventId());
        editor.commit();
        loadSharedPreferencesLogList();
    }


}
