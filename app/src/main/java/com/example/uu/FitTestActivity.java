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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layer_net.stepindicator.StepIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FitTestActivity extends AppCompatActivity implements SectionsPagerAdapter.OnPageListener{

    private final Context mContext=this;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private List<crewObject> crewArrayList;
    private NoSwipeViewPager mViewPager;
    private StepIndicator stepIndicator;
    private List<String> selectedPriority=new ArrayList<>();
    private List<DoneCalculate> recommendCrew=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_test);

        Intent getIntent = getIntent();

        crewArrayList = getIntent.getParcelableArrayListExtra("crewArrayList");


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(mContext);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (NoSwipeViewPager) findViewById(R.id.stepViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        stepIndicator = (StepIndicator) findViewById(R.id.step_indicator);
        stepIndicator.setupWithViewPager(mViewPager);

        // Enable | Disable click on step number
        stepIndicator.setClickable(false);

    }

    @Override
    public void OnFitTestClose() {
        finish();
    }

    @Override
    public void OnNextClicked(List<Integer> checkChips) {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void OnGobackToFirstClicked() {
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void OnCalculateFitnessClicked(List<String> selectedPriority) {
        this.selectedPriority=selectedPriority;
        setRecommendCrew();
        mSectionsPagerAdapter.calculatedCrewList(recommendCrew);
        mViewPager.setCurrentItem(2);
    }
    public void setRecommendCrew(){
        for(int i=0;i<crewArrayList.size();i++){

        }
        recommendCrew.add(new DoneCalculate("b",2F));
        recommendCrew.add(new DoneCalculate("C",1F));
        recommendCrew.add(new DoneCalculate("a", 3F));
        Collections.sort(recommendCrew);
    }
}