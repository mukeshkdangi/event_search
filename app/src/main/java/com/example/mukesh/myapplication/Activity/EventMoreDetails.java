package com.example.mukesh.myapplication.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mukesh.myapplication.Fragment.ArtistTabFragment;
import com.example.mukesh.myapplication.Fragment.EventTabFragment;
import com.example.mukesh.myapplication.Fragment.UpcomingEventsFragment;
import com.example.mukesh.myapplication.Fragment.VenueTabFragment;
import com.example.mukesh.myapplication.POJO.EventDetails;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.ViewPagerAdapter;
import com.google.gson.Gson;

public class EventMoreDetails extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.artist,
            R.drawable.venue,
            R.drawable.upcoming
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_more_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        EventDetails eventDetail = new Gson().fromJson(getIntent().getStringExtra("eventDetails"), EventDetails.class);
        // new GetEventTabDetails().execute(new Gson().toJson(eventDetail));

        Bundle bundle = new Bundle();
        bundle.putString("eventDetails", new Gson().toJson(eventDetail));
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), bundle);

        viewPagerAdapter.addFragment(new EventTabFragment(), "EVENT");
        viewPagerAdapter.addFragment(new ArtistTabFragment(), "ARTIST(S)");
        viewPagerAdapter.addFragment(new VenueTabFragment(), "VENUE");
        viewPagerAdapter.addFragment(new UpcomingEventsFragment(), "UPCOMING");


        EventTabFragment eventTabFragment = new EventTabFragment();
        eventTabFragment.setArguments(bundle);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }


}



