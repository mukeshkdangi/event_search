package com.example.mukesh.myapplication.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mukesh.myapplication.Fragment.FavoriteTabFragment;
import com.example.mukesh.myapplication.Fragment.SearchTabFragment;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {


    static Toolbar toolbar;
    static TabLayout tabLayout;
    static ViewPager viewPager;
    static ViewPagerAdapter viewPagerAdapter;
    static FragmentManager fragmentManager;
    static Bundle bundle = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Search");
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fragmentManager = getSupportFragmentManager();

        bundle.clear();
        viewPagerAdapter = new ViewPagerAdapter(MainActivity.fragmentManager, bundle);
        viewPagerAdapter.addFragment(new SearchTabFragment(), "SEARCH");
        viewPagerAdapter.addFragment(new FavoriteTabFragment(), "FAVORITE");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPagerAdapter.notifyDataSetChanged();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}

