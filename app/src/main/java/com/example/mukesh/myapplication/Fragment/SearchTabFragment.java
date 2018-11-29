package com.example.mukesh.myapplication.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mukesh.myapplication.Activity.EventDetailsPage;
import com.example.mukesh.myapplication.ApiCall;
import com.example.mukesh.myapplication.AutoSuggestAdapter;
import com.example.mukesh.myapplication.POJO.SearchForm;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.Services.GPSTracker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


public class SearchTabFragment extends Fragment {


    public SearchForm searchform;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    public View view;
    public Context context;

    public SearchTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_tab, container, false);
        this.context = view.getContext();
        Spinner spinner = view.findViewById(R.id.category_spinner);

        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Music");
        categories.add("Sports");
        categories.add("Arts & Theatre");
        categories.add("Film");
        categories.add("Miscellaneous");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        Spinner unitSpinner = view.findViewById(R.id.unit_spinner);

        List<String> distanceUnit = new ArrayList<>();
        distanceUnit.add("Miles");
        distanceUnit.add("Kilometer");
        ArrayAdapter<String> unitDataAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_spinner_item, distanceUnit);
        unitDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitDataAdapter);

        view.findViewById(R.id.auto_complete_edit_text).setEnabled(true);
        view.findViewById(R.id.location_desc).setEnabled(false);
        view.findViewById(R.id.location_error).setVisibility(View.GONE);
        view.findViewById(R.id.keyword_error).setVisibility(View.GONE);

        view.findViewById(R.id.auto_complete_edit_text).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                view.findViewById(R.id.keyword_error).setVisibility(View.GONE);
                return false;
            }
        });

        view.findViewById(R.id.location_desc).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                view.findViewById(R.id.location_error).setVisibility(View.GONE);
                return false;
            }
        });

        view.findViewById(R.id.from_here).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.location_desc).setEnabled(false);
                view.findViewById(R.id.location_error).setVisibility(View.GONE);
            }

        });

        view.findViewById(R.id.from_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.location_desc).setEnabled(true);
            }
        });

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) this.context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        }

        final AppCompatAutoCompleteTextView autoCompleteTextView =
                view.findViewById(R.id.auto_complete_edit_text);
        final TextView selectedText = view.findViewById(R.id.selected_item);

        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(this.context,
                android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectedText.setText(autoSuggestAdapter.getObject(position));
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        view.findViewById(R.id.search_bn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                searchEvents(view);
            }
        });
        view.findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearAllFields(view);
            }
        });

        return view;
    }


    private void makeApiCall(String text) {
        ApiCall.make(this.context, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.optJSONObject("_embedded").getJSONArray("attractions");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void clearAllFields(View view1) {
        ((TextView) view.findViewById(R.id.auto_complete_edit_text)).setText("");
        ((TextView) view.findViewById(R.id.distance)).setText("");
        ((Spinner) view.findViewById(R.id.category_spinner)).setSelection(0);
        ((Spinner) view.findViewById(R.id.unit_spinner)).setSelection(0);
        ((TextView) view.findViewById(R.id.location_desc)).setText("");
        view.findViewById(R.id.location_desc).setEnabled(false);
        view.findViewById(R.id.location_error).setVisibility(View.GONE);
        view.findViewById(R.id.keyword_error).setVisibility(View.GONE);
        ((RadioGroup) view.findViewById(R.id.radioLocation)).check(R.id.from_here);
    }


    public void searchEvents(View view1) {
        RadioGroup radioGroup;

        Pattern p = Pattern.compile("[^a-zA-Z0-9' ]");
        boolean isInValidInput = false;
        String keyword = ((EditText) view.findViewById(R.id.auto_complete_edit_text)).getText().toString();
        if (keyword == null || keyword.length() <= 0 || p.matcher(keyword).find()) {
            view.findViewById(R.id.keyword_error).setVisibility(View.VISIBLE);
            isInValidInput = true;
        }

        boolean isLocaDecPresent = false;
        if (view.findViewById(R.id.location_desc).isEnabled()) {
            String locaDec = ((EditText) view.findViewById(R.id.location_desc)).getText().toString();
            if (locaDec == null || locaDec.length() <= 0) {
                view.findViewById(R.id.location_error).setVisibility(View.VISIBLE);
                isInValidInput = true;
            }
            isLocaDecPresent = true;
        }
        if (isInValidInput) {
            Toast.makeText(view.getContext(), "Please fix all fields with errors",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Spinner spinner = view.findViewById(R.id.category_spinner);
        String category = spinner.getSelectedItem().toString();
        int distance = 10;

        Editable textView = ((EditText) view.findViewById(R.id.distance)).getText();
        if (!textView.toString().equalsIgnoreCase("") && Objects.nonNull(textView)) {
            distance = Integer.valueOf(((EditText) view.findViewById(R.id.distance)).getText().toString());
        }

        spinner = view.findViewById(R.id.unit_spinner);
        String unit = spinner.getSelectedItem().toString();

        searchform = new SearchForm();
        searchform.setKeyword(keyword);
        searchform.setCategory(category);
        searchform.setDistance(distance);
        searchform.setDistanceUnit(unit);

        radioGroup = view.findViewById(R.id.radioLocation);

        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (((RadioButton) view.findViewById(selectedId)).getText().toString().contains("Other")) {
            searchform.setOtherLocation(true);
        } else {
            GPSTracker gps = new GPSTracker(view.getContext());
            if (gps != null) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                searchform.setLat(latitude);
                searchform.setLon(longitude);
            }
        }

        if (isLocaDecPresent) {
            searchform.setLocationDescription(((EditText) view.findViewById(R.id.location_desc)).getText().toString());
        }


        Intent newIntent = new Intent(this.context, EventDetailsPage.class);
        EventDetailsPage.initList();
        newIntent.putExtra("searchForm", new Gson().toJson(searchform));
        // searchform = new Gson().fromJson(getIntent().getStringExtra("searchForm"), SearchForm.class);
        startActivity(newIntent);

    }
}