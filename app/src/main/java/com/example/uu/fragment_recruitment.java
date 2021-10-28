package com.example.uu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class fragment_recruitment extends Fragment implements DrawingMapActivity.OnBitmapCreated{
    private View linear_recruitment;
    private View linear_crew;
    private LinearLayout linear_dialog;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<recruit_object> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ViewGroup rootview;

    private ArrayAdapter guAdapter;
    private Spinner guSpinner;
    private RecyclerView crewRecyclerView;
    private RecyclerView.Adapter crewAdapter;
    private RecyclerView.LayoutManager crewLayoutManager;
    private ArrayList<crewObject> crewArrayList;
    private ArrayList<crewObject> filteredList;
    private String currentCrew;

    ImageView show_map;
    private FirebaseAuth mFirebaseAuth;
    String selectedGu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview=(ViewGroup) inflater.inflate(R.layout.fragment_recruitment,container,false);

        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        linear_recruitment= (LinearLayout) rootview.findViewById(R.id.linear_Recruitment);
        linear_crew= (LinearLayout) rootview.findViewById(R.id.linear_crew);
        linear_crew.setVisibility(View.INVISIBLE);

        ImageButton show_recruitment=(ImageButton) rootview.findViewById(R.id.show_recruitment);
        ImageButton show_crew=(ImageButton) rootview.findViewById(R.id.show_crew);

        show_recruitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_crew.setBackgroundResource(R.drawable.ic_crew);
                linear_recruitment.setVisibility(View.VISIBLE);
                linear_crew.setVisibility(View.INVISIBLE);
            }
        });

        show_crew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_crew.setBackgroundResource(R.drawable.ic_crew_selected);
                linear_recruitment.setVisibility(View.INVISIBLE);
                linear_crew.setVisibility(View.VISIBLE);
            }
        });


        //******* linear_recruitment 부분 코딩

        recyclerView = rootview.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        if(firebaseUser != null) {
            databaseReference = database.getReference("Recruit");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // DB data를 받아오는곳
                    arrayList.clear(); // 기존 배열리스트 초기화
                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                        recruit_object recruit = Snapshot.getValue(recruit_object.class);
                        arrayList.add(recruit);
                    }
                    adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //DB 받아오던 중 에러 발생하는 경우
                    Log.e("Error", String.valueOf(error.toException()));
                }
            });

            adapter = new recruitAdapter(arrayList, getContext());
            recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        Button recruit=(Button)rootview.findViewById(R.id.recruit);
        recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog dialog = new customDialog(getActivity());
                dialog.show();
            }
        });

        }
        else{

            Toast.makeText(rootview.getContext(), "Need to login", Toast.LENGTH_SHORT).show();

            fragment_login login = new fragment_login();
            ((MainActivity)getActivity()).replaceFragment(login);
        }



        //******* linear_crew 부분 코딩




        //크루 존재 여부 check
        /*
        databaseReference = database.getReference("UU");
        databaseReference.child("UserAccount").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userObject info = snapshot.getValue(userObject.class);
                currentCrew = info.getCurrentCrew();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */


        //유저가 크루가 없는 경우 초기 화면
        if (currentCrew == "none"){
        }

        //지역구별 검색 스피너
        guSpinner = (Spinner) rootview.findViewById(R.id.guSpinner);
        guAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seoul_gu, android.R.layout.simple_spinner_item);
        guSpinner.setAdapter(guAdapter);



        Button createCrewBtn =(Button) rootview.findViewById(R.id.crewAdd);
        createCrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                crewAddDialog crewDialog = new crewAddDialog();
                //crewDialog.show(getActivity().getFragmentManager(), "test");
                crewDialog.show(getChildFragmentManager(), "crew");
            }
        });

        //크루 리스트 show
        crewRecyclerView = rootview.findViewById(R.id.crewRecyclerView);
        crewRecyclerView.setHasFixedSize(true);
        crewLayoutManager = new LinearLayoutManager(getContext());
        crewRecyclerView.setLayoutManager(crewLayoutManager);
        crewArrayList = new ArrayList<>();
        filteredList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Crew");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // DB data를 받아오는곳
                crewArrayList.clear(); // 기존 배열리스트 초기화
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                    crewObject recruit = Snapshot.getValue(crewObject.class);
                    crewArrayList.add(recruit);
                }
                crewAdapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DB 받아오던 중 에러 발생하는 경우
                Log.e("Error", String.valueOf(error.toException()));
            }
        });

        guSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGu = (String) adapterView.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        crewAdapter = new crewAdapter(crewArrayList, getContext());
        crewRecyclerView.setAdapter(crewAdapter); //리사이클러뷰에 어댑터 연결

        Button crewSearchBtn = rootview.findViewById(R.id.searchCrewBtn);
        crewSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // DB data를 받아오는곳
                        crewArrayList.clear(); // 기존 배열리스트 초기화
                        for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                            crewObject recruit = Snapshot.getValue(crewObject.class);
                            if( recruit.getLocation().equals(selectedGu)){
                                crewArrayList.add(recruit);
                            }

                        }
                        crewAdapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //DB 받아오던 중 에러 발생하는 경우
                        Log.e("Error", String.valueOf(error.toException()));
                    }
                });

            }
        });


        //크루에 속한 유저 초기화면


        return rootview;
    }

    @Override
    public void saveBitmap(Bitmap bm) {
        //show_map=(ImageView)rootview.findViewById(R.id.testMap);
        show_map.setImageBitmap(bm);
    }

    public void searchFilter(String searchText) {


        for (int i = 0; i < crewArrayList.size(); i++) {
            if (crewArrayList.get(i).getLocation().equals(selectedGu)) {
                filteredList.add(crewArrayList.get(i));
            }
        }

        //crewAdapter.filterList(filteredList);
    }


}
