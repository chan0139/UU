package com.example.uu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class fragment_recruitment_crew extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<userObject> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private String userName;
    private String userCrew;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_recruitment_crew,container,false);



        recyclerView = rootview.findViewById(R.id.crewUserRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        databaseReference = database.getReference("UU");


        if(firebaseUser != null) {
            databaseReference = database.getReference("UU");

            databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("currentCrew").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userCrew = snapshot.getValue(String.class);
                    databaseReference.child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // DB data를 받아오는곳
                            arrayList.clear(); // 기존 배열리스트 초기화
                            for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                                userObject info = Snapshot.getValue(userObject.class);
                                if(userCrew.equals(info.getCurrentCrew())){
                                    arrayList.add(info);
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
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            adapter = new crewUserAdapter(arrayList);
            recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        }
        return rootview;
    }


}