package com.example.mukesh.myapplication.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mukesh.myapplication.FavEventDetailsAdaptor;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.Storage.SharedPreferenceConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Mukesh Dangi
 */

public class FavoriteTabFragment extends Fragment {
    public View view;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public FavEventDetailsAdaptor adaptor;
    public List<EventDetails> eventDetails = new ArrayList<>();
    public Context applicationCtx;


    public FavoriteTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        eventDetails = new SharedPreferenceConfig(applicationCtx).loadSharedPreferencesLogList();
        adaptor.setUpdateChange(eventDetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorite_tab, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        applicationCtx = view.getContext();

        //view.setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.fav_recyclerview);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        eventDetails = new SharedPreferenceConfig(applicationCtx).loadSharedPreferencesLogList();
        adaptor = new FavEventDetailsAdaptor(eventDetails, applicationCtx);
        recyclerView.setAdapter(adaptor);

        return view;
    }

}
