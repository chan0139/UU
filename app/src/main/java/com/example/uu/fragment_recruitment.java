package com.example.uu;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class fragment_recruitment extends Fragment{
    private View linear_recruitment;
    private View linear_lounge;

    private ImageButton show_recruitment;
    private ImageButton show_lounge;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<recruit_object> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceRecruit;
    private DatabaseReference databaseReferenceUser;
    private ViewGroup rootview;

    ArrayList<String> userRecruitList;
    private ArrayList<recruit_object> loungeArrayList;
    private RecyclerView loungeRecruitRecyclerView;
    private RecyclerView.LayoutManager loungeLayoutManager;
    private RecyclerView.Adapter loungeAdapter;
    public String currentCrew;
    private FirebaseAuth mFirebaseAuth;

    int which_layout=R.id.show_recruitment;

    private ArrayAdapter spinnerAdapter;
    private String selectedGu;
    private TextView title;



    fragment_recruitment(int id_layout){
        which_layout=id_layout;
    }

    public fragment_recruitment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview=(ViewGroup) inflater.inflate(R.layout.fragment_recruitment,container,false);
        title = getActivity().findViewById(R.id.title);

        ImageView recruitGif = (ImageView) rootview.findViewById(R.id.recruitGif);
        Glide.with(this).load(R.raw.finding_route).into(recruitGif);
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        databaseReference = database.getReference("UU");

        if(firebaseUser == null){
            Toast.makeText(rootview.getContext(), "Need to login", Toast.LENGTH_SHORT).show();
            title.setText("Login");
            fragment_login login = new fragment_login();
            ((MainActivity) getActivity()).replaceFragment(login);
            return rootview;
        }


        databaseReference.child("UserAccount").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userObject info = snapshot.getValue(userObject.class);
                currentCrew = info.getCurrentCrew();
                layoutConverter(which_layout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        linear_recruitment = (LinearLayout) rootview.findViewById(R.id.linear_Recruitment);
        linear_lounge=(LinearLayout)rootview.findViewById(R.id.linear_lounge);

        show_recruitment = (ImageButton) rootview.findViewById(R.id.show_recruitment);
        show_lounge=(ImageButton) rootview.findViewById(R.id.show_lounge);

        show_recruitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutConverter(R.id.show_recruitment);
            }
        });

        show_lounge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutConverter(R.id.show_lounge);
            }
        });

        //******* linear_recruitment 부분 코딩

        Spinner recruitSpinner = (Spinner) rootview.findViewById(R.id.recruitSpinner);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seoul_gu, android.R.layout.simple_spinner_dropdown_item);
        recruitSpinner.setAdapter(spinnerAdapter);

        recyclerView = rootview.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReferenceRecruit = database.getReference("Recruit");

        recruitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGu = (String) adapterView.getSelectedItem();
                databaseReferenceRecruit.orderByChild("date").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // DB data를 받아오는곳
                        arrayList.clear(); // 기존 배열리스트 초기화
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            recruit_object recruit = Snapshot.getValue(recruit_object.class);
                            if(recruit.getCurrentUserNum() == recruit.getTotalUserNum()) continue;
                            if(selectedGu.equals("지역선택")){
                                arrayList.add(recruit);
                                continue;
                            }
                            if (recruit.getOrigin().equals(selectedGu)) {
                                arrayList.add(recruit);
                            }


                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //DB 받아오던 중 에러 발생하는 경우
                        Log.e("Error", String.valueOf(error.toException()));
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        adapter = new recruitAdapter(arrayList, getContext(), 0);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결


        RecyclerDecoration spaceDecoration = new RecyclerDecoration(30);
        recyclerView.addItemDecoration(spaceDecoration);

        TextView recruit = (TextView) rootview.findViewById(R.id.recruit);
        recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog dialog = new customDialog();
                dialog.show(getChildFragmentManager(), "recruit");
            }
        });
        //

        //******* linear_lounge 부분 코딩

        ImageView chatGif = (ImageView) rootview.findViewById(R.id.chatGif);
        Glide.with(this).load(R.raw.chat).into(chatGif);
        userRecruitList = new ArrayList<>();
        loungeArrayList = new ArrayList<>();
        loungeRecruitRecyclerView = rootview.findViewById(R.id.joinedRunning);
        loungeRecruitRecyclerView.setHasFixedSize(true);
        loungeLayoutManager = new LinearLayoutManager(getContext());
        loungeRecruitRecyclerView.setLayoutManager(loungeLayoutManager);
        databaseReferenceUser = database.getReference("UU");
        databaseReferenceUser.child("UserAccount").child(firebaseUser.getUid()).child("recruitList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRecruitList.clear();
                for (DataSnapshot snapshotNode: snapshot.getChildren()) {
                    String getUserRecruit = (String) snapshotNode.getKey();
                    userRecruitList.add(getUserRecruit);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceRecruit.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // DB data를 받아오는곳
                loungeArrayList.clear(); // 기존 배열리스트 초기화
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    recruit_object recruit = Snapshot.getValue(recruit_object.class);
                    for(int i = 0; i < userRecruitList.size(); i++){
                        if(userRecruitList.get(i).equals(recruit.getRecruitId())){
                            loungeArrayList.add(recruit);
                        }
                    }

                }
                loungeAdapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DB 받아오던 중 에러 발생하는 경우
                Log.e("Error", String.valueOf(error.toException()));
            }
        });

        loungeAdapter = new recruitAdapter(loungeArrayList, getContext(),1);

        loungeRecruitRecyclerView.setAdapter(loungeAdapter); //리사이클러뷰에 어댑터 연결
        loungeRecruitRecyclerView.addItemDecoration(spaceDecoration);

        return rootview;
    }


    public void layoutConverter(int which_layout){
        if(which_layout==R.id.show_recruitment){
            show_lounge.setBackgroundResource(R.drawable.ic_lounge);
            linear_recruitment.setVisibility(View.VISIBLE);
            linear_lounge.setVisibility(View.INVISIBLE);
        }
        else if(which_layout==R.id.show_lounge){
            show_lounge.setBackgroundResource(R.drawable.ic_lounge_selected);
            linear_recruitment.setVisibility(View.INVISIBLE);
            linear_lounge.setVisibility(View.VISIBLE);
        }

    }


}
