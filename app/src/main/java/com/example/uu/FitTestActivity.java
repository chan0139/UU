package com.example.uu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.layer_net.stepindicator.StepIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
interface IntegerPriority{
    int averageRunningTime=1;
    int averageRunningDistance=2;
    int whatDayDoYouRun=3;
    int whatTimeDoYouRun=4;
    int whereDoYouRun=5;
}

public class FitTestActivity extends AppCompatActivity implements SectionsPagerAdapter.OnPageListener{

    private final Context mContext=this;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private List<crewObject> crewArrayList;
    private NoSwipeViewPager mViewPager;
    private StepIndicator stepIndicator;
    private int[] integerPriority;
    private List<String> selectedPriority=new ArrayList<>();
    private List<DoneCalculate> recommendCrew=new ArrayList<>();

    private FitTestData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_test);
        findViewById(R.id.needRecord).setVisibility(View.INVISIBLE);

        Intent getIntent = getIntent();

        crewArrayList = getIntent.getParcelableArrayListExtra("crewArrayList");

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = database.getReference("UU");
        databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("FitTest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myData=snapshot.getValue(FitTestData.class);
                if(myData==null){
                    findViewById(R.id.needRecord).setVisibility(View.VISIBLE);
                    //insert animation
                    findViewById(R.id.step_indicator).setVisibility(View.INVISIBLE);
                    findViewById(R.id.stepViewPager).setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.gotoRecruitment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(777,new Intent());
                finish();
            }
        });
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
        prioritytoInteger();
        float score;
        int sumOfPriority=0;
        for(int i=0;i<crewArrayList.size();i++){
            score=0;
            FitTestData crewData=crewArrayList.get(i).getFitTestData();
            for (int j=0;j<integerPriority.length;j++){
                sumOfPriority=sumOfPriority+1;
                switch (integerPriority[j]){
                    case IntegerPriority.averageRunningTime:
                        int x=Math.abs(crewData.getRunningTime()-myData.getRunningTime());
                        float result=(float) x*x/36;
                        score+=result;
                        break;
                    case IntegerPriority.averageRunningDistance:
                    case IntegerPriority.whatDayDoYouRun:
                    case IntegerPriority.whatTimeDoYouRun:
                    case IntegerPriority.whereDoYouRun:
                }
            }
            score=score/sumOfPriority;
            recommendCrew.add(new DoneCalculate(crewArrayList.get(i).getCrewName(),score));
        }
        Collections.sort(recommendCrew);
    }
    public void prioritytoInteger(){
        integerPriority=new int[selectedPriority.size()];
        for(int i=0;i<selectedPriority.size();i++){
            switch (selectedPriority.get(i)){
                case "Average Running Time":
                    integerPriority[i]=IntegerPriority.averageRunningTime;
                    break;
                case "Average Running Distance":
                    integerPriority[i]=IntegerPriority.averageRunningDistance;
                    break;
                case "What Day do you usually run":
                    integerPriority[i]=IntegerPriority.whatDayDoYouRun;
                    break;
                case "What Time do you usually run":
                    integerPriority[i]=IntegerPriority.whatTimeDoYouRun;
                    break;
                case "Where do you usually run":
                    integerPriority[i]=IntegerPriority.whereDoYouRun;
            }
        }
    }
}