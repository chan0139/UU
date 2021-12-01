package com.example.uu;

import android.app.Activity;
import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
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
    private String userName;
    private ArrayList<recruit_object> arrayList;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> userRecruitList;
    private String currentCrew;
    private FragmentActivity myContext;
    private TextView title;

    @Override
    public void onAttach(@NonNull Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.bar_profile,container,false);
        Button button = rootview.findViewById(R.id.logoutButton);
        TextView email = rootview.findViewById(R.id.email);
        ImageView profile = rootview.findViewById(R.id.profile);
        TextView crew=rootview.findViewById(R.id.textCrew);
        TextView name = rootview.findViewById(R.id.name);
        title = getActivity().findViewById(R.id.title);

        recyclerView = rootview.findViewById(R.id.profileRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        arrayList = new ArrayList<>();
        userRecruitList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {

            profile.setImageResource(R.drawable.no_login_user_image);
            name.setText("  익 명");
            email.setText("이 메 일");
            button.setText("로그인 먼저 진행해주세요.");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    title.setText("Login");
                    fragment_login fragment_login = new fragment_login();
                    myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_login).commit();
                }
            });

            return rootview;
        }

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


        String uid = user != null ? user.getUid() : null;


        mDatabaseRef.child("UserAccount").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userObject info = snapshot.getValue(userObject.class);
                userId = info.getUserId();
                userName = info.getUserName();
                userProfileUrl = info.getUserProfileUrl();
                currentCrew = info.getCurrentCrew();
                if (currentCrew!="none")
                    crew.setText(currentCrew);
                email.setText(userId);
                name.setText(userName);
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

        adapter = new recruitAdapter(arrayList, getContext(), 2);

        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        RecyclerDecoration spaceDecoration = new RecyclerDecoration(30);
        recyclerView.addItemDecoration(spaceDecoration);

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
                title.setText("Login");
            }
        });





        return rootview;
    }
}
