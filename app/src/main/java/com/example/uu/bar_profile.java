package com.example.uu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;

public class bar_profile extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference databaseReferenceRecruit;
    private String userId, userProfileUrl;
    private String userName, userGender;
    private ArrayList<recruit_object> arrayList;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> userRecruitList;
    private String currentCrew;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.bar_profile,container,false);
        Button button = rootview.findViewById(R.id.logoutButton);
        TextView email = rootview.findViewById(R.id.email);
        ImageView profile = rootview.findViewById(R.id.profile);
        TextView name = rootview.findViewById(R.id.name);
        TextView gender = rootview.findViewById(R.id.gender);

        recyclerView = rootview.findViewById(R.id.profileRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        arrayList = new ArrayList<>();
        userRecruitList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseRef.child("UserAccount").child(user.getUid()).child("recruitList").addListenerForSingleValueEvent(new ValueEventListener() {
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

        if(user != null) {
            String uid = user != null ? user.getUid() : null;


            mDatabaseRef.child("UserAccount").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userObject info = snapshot.getValue(userObject.class);
                    userId = info.getUserId();
                    userName = info.getUserName();
                    userGender = info.getUserGender();
                    userProfileUrl = info.getUserProfileUrl();
                    currentCrew = info.getCurrentCrew();
                    email.setText(userId);
                    name.setText(userName);
                    gender.setText("/  " + userGender);
                    Glide.with(getContext()).load(userProfileUrl).into(profile);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            databaseReferenceRecruit = database.getReference("Recruit");

            databaseReferenceRecruit.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // DB data를 받아오는곳
                    arrayList.clear(); // 기존 배열리스트 초기화
                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                        recruit_object recruit = Snapshot.getValue(recruit_object.class);
                        for(int i = 0; i < userRecruitList.size(); i++){
                            if(userRecruitList.get(i).equals(recruit.getRecruitId())){
                                arrayList.add(recruit);
                            }
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

            adapter = new recruitAdapter(arrayList, getContext());

            recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결


        }
        else{
                profile.setImageResource(R.drawable.no_login_user_image);
                name.setText("  익 명");
                gender.setText("/  성 별");
                email.setText("이 메 일");
                button.setText("로그인 먼저 진행해주세요.");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)getActivity()).replaceFragment(fragment_login.newInstance());
                    }
                });
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        mFirebaseAuth.signOut();
                        ((MainActivity)getActivity()).replaceFragment(fragment_login.newInstance());
                    }
                });
            }
        });

        return rootview;
    }
}
