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

import android.annotation.SuppressLint;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

interface IntegerPriority{
    int averageRunningTime=1;
    int averageRunningDistance=2;
    int whatDayDoYouRun=3;
    int whatTimeDoYouRun=4;
    int whereDoYouRun=5;
}

public class FitTestActivity extends AppCompatActivity implements SectionsPagerAdapter.OnPageListener{
    private FirebaseDatabase database;
    private DatabaseReference crewDatabaseReference;

    private final Context mContext=this;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private List<crewObject> crewArrayList;
    private List<FitTestData> crewFitTestDataList=new ArrayList<>();
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

        database=FirebaseDatabase.getInstance();

        Intent getIntent = getIntent();

        crewArrayList = getIntent.getParcelableArrayListExtra("crewArrayList");

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

        crewDatabaseReference=database.getReference("Crew");
        for(int i=0;i<crewArrayList.size();i++){
            crewDatabaseReference.child(crewArrayList.get(i).getCrewName()).child("FitTest").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FitTestData crewData=snapshot.getValue(FitTestData.class);
                    crewFitTestDataList.add(crewData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


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

    @SuppressLint("NewApi")
    public void setRecommendCrew(){
        prioritytoInteger();
        float score;
        int sumOfPriority=0;
        for(int i=0;i<crewFitTestDataList.size();i++){
            score=0;
            FitTestData crewData=crewFitTestDataList.get(i);
            Log.d("testkang",crewData.getCrewName());
            int x;
            float result;
            if(crewData.getNumberOfRunning()>0){
                for (int j=0;j<integerPriority.length;j++){
                    sumOfPriority=sumOfPriority+j+1;
                    result=0;
                    x=0;
                    switch (integerPriority[j]){
                        case IntegerPriority.averageRunningTime:
                            x=Math.abs(crewData.getRunningTime()-myData.getRunningTime());
                            if(x<60){
                                x=x-60;
                            }
                            else{
                                //차이 60min 넘으면 0점
                                x=0;
                                break;
                            }
                            result=(float) x*x/36;
                            result=result*(integerPriority.length-j);
                            score+=result;
                            break;
                        case IntegerPriority.averageRunningDistance:
                            x=Math.abs(crewData.getDistance()-myData.getDistance());
                            if(x<1000){
                                x=x*x;
                            }
                            else{
                                //차이 1000m 넘으면 0점
                                x=1000000;
                                break;
                            }
                            result=(float) x/10000;
                            Log.d("testresult",score+"");
                            result=(100-result)*(integerPriority.length-j);
                            score+=result;
                            break;
                        case IntegerPriority.whatDayDoYouRun:
                            //<# of running you did at that day,day>
                            List<Integer> mostFavoriteDay = new ArrayList<>(myData.getDay());
                            mostFavoriteDay.sort(Collections.reverseOrder());
                            int myFavoriteDay=mostFavoriteDay.get(0);

                            mostFavoriteDay.clear();
                            mostFavoriteDay.addAll(crewData.getDay());
                            mostFavoriteDay.sort(Collections.reverseOrder());
                            int crewFavoriteDay=mostFavoriteDay.get(0);

                            Map<Integer,Integer> myDay=new LinkedHashMap<>();
                            Map<Integer,Integer>crewDay=new LinkedHashMap<>();
                            for(int k=0;k<7;k++){
                                myDay.put(myData.getDay().get(k),k);
                                crewDay.put(crewData.getDay().get(k),k);
                            }
                            myDay=sortMapByKey(myDay);
                            crewDay=sortMapByKey(crewDay);

                            if(myDay.get(myFavoriteDay)==crewDay.get(crewFavoriteDay)){
                                result=100;
                            }
                            else{
                                int numOfOverlap=0;
                                for(Map.Entry<Integer,Integer>entry:myDay.entrySet()){
                                    if(crewDay.containsValue(entry.getValue())){
                                        numOfOverlap+=1;
                                    }
                                }
                                switch (numOfOverlap){
                                    case 0:
                                        result=0;
                                        break;
                                    case 1:
                                        result=50;
                                        break;
                                    case 2:
                                        result=70;
                                        break;
                                    case 3:
                                        result=90;
                                        break;
                                }
                            }
                            result=result*(integerPriority.length-j);
                            score+=result;
                            break;
                        case IntegerPriority.whatTimeDoYouRun:
                            //<# of running you did at that startTime,startTime>
                            List<Integer> mostFavoriteTime = new ArrayList<>(myData.getStartTime());
                            mostFavoriteTime.sort(Collections.reverseOrder());
                            int myFavoriteTime=mostFavoriteTime.get(0);

                            mostFavoriteTime.clear();
                            mostFavoriteTime.addAll(crewData.getStartTime());
                            mostFavoriteTime.sort(Collections.reverseOrder());
                            int crewFavoriteTime=mostFavoriteTime.get(0);

                            Map<Integer,Integer> myStartTime=new LinkedHashMap<>();
                            Map<Integer,Integer>crewStartTime=new LinkedHashMap<>();
                            for(int k=0;k<24;k++){
                                myStartTime.put(myData.getStartTime().get(k),k);
                                crewStartTime.put(crewData.getStartTime().get(k),k);
                            }
                            myStartTime=sortMapByKey(myStartTime);
                            crewStartTime=sortMapByKey(crewStartTime);

                            if(myStartTime.get(myFavoriteTime)==crewStartTime.get(crewFavoriteTime)){
                                result=100;
                            }
                            else{
                                int numOfOverlap=0;
                                for(Map.Entry<Integer,Integer>entry:myStartTime.entrySet()){
                                    if(crewStartTime.containsValue(entry.getValue())){
                                        numOfOverlap+=1;
                                    }
                                }
                                switch (numOfOverlap){
                                    case 0:
                                        result=0;
                                        break;
                                    case 1:
                                        result=50;
                                        break;
                                    case 2:
                                        result=70;
                                        break;
                                    case 3:
                                        result=90;
                                        break;
                                }
                            }
                            result=result*(integerPriority.length-j);
                            score+=result;
                            break;
                        case IntegerPriority.whereDoYouRun:

                    }
                }
                score=score/sumOfPriority;
            }
            Log.d("testkang","score : "+score);
            recommendCrew.add(new DoneCalculate(crewFitTestDataList.get(i).getCrewName(),score));
        }
        Collections.sort(recommendCrew);
    }
    public static LinkedHashMap<Integer, Integer> sortMapByKey(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> entries = new LinkedList<>(map.entrySet());
        Collections.sort(entries, (o1, o2) -> o2.getKey().compareTo(o1.getKey()));

        LinkedHashMap<Integer, Integer> result = new LinkedHashMap<>();
        int size = 3;
        for (Map.Entry<Integer, Integer> entry : entries) {
            if (size > 0) {
                result.put(entry.getKey(), entry.getValue());
            }
            size -= 1;

        }
        return result;
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