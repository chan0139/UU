package com.example.uu;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class fragment_recruitment extends Fragment {
    private View linear_recruitment;
    private View linear_crew;
    private LinearLayout linear_dialog;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<recruit_object> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_recruitment,container,false);
        recyclerView = rootview.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
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
        Log.e("cnt",String.valueOf(adapter.getItemCount()));
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        Button recruit=(Button)rootview.findViewById(R.id.recruit);
        recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog dialog = new customDialog(getActivity());

                dialog.show();
            }
        });

        linear_recruitment=(LinearLayout)rootview.findViewById(R.id.linear_Recruitment);
        linear_crew=(LinearLayout)rootview.findViewById(R.id.linear_crew);
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

        return rootview;
    }
}
