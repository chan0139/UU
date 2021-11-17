package com.example.uu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layer_net.stepindicator.StepIndicator;


public class FitTestActivity extends AppCompatActivity {

    private final Context mContext=this;

    private FitTestData targetCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_test);

        Intent getIntent = getIntent();

        targetCrew = getIntent.getParcelableExtra("targetCrew");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(mContext,targetCrew);
        // Set up the ViewPager with the sections adapter.
        NoSwipeViewPager mViewPager = (NoSwipeViewPager) findViewById(R.id.stepViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        StepIndicator stepIndicator = (StepIndicator) findViewById(R.id.step_indicator);
        stepIndicator.setupWithViewPager(mViewPager);

        // Enable | Disable click on step number
        stepIndicator.setClickable(false);


    }

}