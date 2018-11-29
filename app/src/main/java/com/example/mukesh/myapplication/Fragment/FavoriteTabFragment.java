package com.example.mukesh.myapplication.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mukesh.myapplication.FavEventDetailsAdaptor;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.Storage.SharedPreferenceConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author : Mukesh Dangi
 */

public class FavoriteTabFragment extends Fragment {
    public static  View view;
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
        if (Objects.nonNull(view) && (Objects.isNull(eventDetails) || eventDetails.size() == 0)) {
            view.findViewById(R.id.no_fav).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (Objects.nonNull(view) && (Objects.isNull(eventDetails) || eventDetails.size() == 0)) {
            view.findViewById(R.id.no_fav).setVisibility(View.VISIBLE);
        }
    }

    public static void showNoFavMessage() {
        view.findViewById(R.id.no_fav).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_favorite_tab, container, false);
        applicationCtx = view.getContext();
        view.findViewById(R.id.no_fav).setVisibility(View.GONE);

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
