package com.example.uu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class fragment_crew extends Fragment{
    private View linear_recruitment;
    private View linear_crew_no;
    private View linear_crew_yes;
    private View linear_lounge;

    private ImageButton show_recruitment;
    private ImageButton show_crew;
    private ImageButton show_lounge;

    private TextView detail_name;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<recruit_object> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceRecruit;
    private DatabaseReference databaseReferenceCrew;
    private DatabaseReference databaseReferenceUser;
    private FirebaseStorage storage;
    private ViewGroup rootview;

    private ArrayAdapter guAdapter;
    private Spinner guSpinner;
    private RecyclerView crewRecyclerView;
    private RecyclerView.Adapter crewAdapter;
    private RecyclerView.LayoutManager crewLayoutManager;
    private ArrayList<crewObject> crewArrayList;
    private ArrayList<crewObject> filteredList;
    ArrayList<String> userRecruitList;
    public String currentCrew;
    private String getCrewName;
    private String getCrewExp;
    private String getCrewImg;
    private String getCrewUserNum;
    private String getCrewLoc;
    private StorageReference crewYesImg;
    private ImageView crewImg;
    public int crewNum;

    public OnCrewAddedListener crewAddedListener;
    interface OnCrewAddedListener{
        void  OnCrewAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        crewAddedListener=(OnCrewAddedListener) context;
    }

    private Bitmap bitmapOfMap;
    private FirebaseAuth mFirebaseAuth;
    String selectedGu;
    private TextView title;

    int which_layout=R.id.show_recruitment;


    fragment_crew(int id_layout){
        which_layout=id_layout;
    }

    public fragment_crew() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview=(ViewGroup) inflater.inflate(R.layout.fragment_crew,container,false);
        title = getActivity().findViewById(R.id.title);

        //ImageView crewGif = (ImageView) rootview.findViewById(R.id.crewGif);
        //Glide.with(this).load(R.raw.friends).into(crewGif);

        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        ImageView runGif = (ImageView) rootview.findViewById(R.id.crewYesCrewImage);
        Glide.with(this).load(R.raw.loading).into(runGif);

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
                Log.d("lastFix",""+info.getCurrentCrew());
                currentCrew = info.getCurrentCrew();
                layoutConverter(which_layout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        detail_name=(TextView)rootview.findViewById(R.id.detail_name);
        linear_crew_no = (LinearLayout) rootview.findViewById(R.id.linear_crew_no);
        linear_crew_yes = (LinearLayout) rootview.findViewById(R.id.linear_crew_yes);

        show_crew = (ImageButton) rootview.findViewById(R.id.show_crew);
        show_lounge=(ImageButton) rootview.findViewById(R.id.show_lounge);

        show_crew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutConverter(R.id.show_crew);
            }
        });

        show_lounge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutConverter(R.id.show_lounge);
            }
        });


        //

        //유저가 크루가 없는 경우 초기 화면

        //지역구별 검색 스피너
        guSpinner = (Spinner) rootview.findViewById(R.id.guSpinner);
        guAdapter = ArrayAdapter.createFromResource(getContext(), R.array.seoul_gu, android.R.layout.simple_spinner_dropdown_item);
        guSpinner.setAdapter(guAdapter);


        TextView createCrewBtn = (TextView) rootview.findViewById(R.id.crewAdd);
        createCrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crewAddDialog crewDialog = new crewAddDialog();
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
        databaseReference = database.getReference("Crew");



        guSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGu = (String) adapterView.getSelectedItem();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // DB data를 받아오는곳
                        crewArrayList.clear(); // 기존 배열리스트 초기화
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            crewObject crew = Snapshot.getValue(crewObject.class);

                            /*kangtest*/
                            //crew.setFitTestData(new FitTestData(1330,55,new Address()));
                            /*kangtest*/
                            if(crew.getTotalUserNum() == 0){
                                databaseReference.removeValue();
                            }

                            if(selectedGu.equals("지역선택")){
                                crewArrayList.add(crew);
                                continue;
                            }
                            if (crew.getLocation().equals(selectedGu)) {
                                crewArrayList.add(crew);
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        crewAdapter = new crewAdapter(crewArrayList, getContext());
        crewRecyclerView.setAdapter(crewAdapter); //리사이클러뷰에 어댑터 연결

        RecyclerDecoration spaceDecoration = new RecyclerDecoration(30);
        crewRecyclerView.addItemDecoration(spaceDecoration);

        rootview.findViewById(R.id.fittest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),FitTestActivity.class);
                intent.putParcelableArrayListExtra("crewArrayList", (ArrayList<? extends Parcelable>) crewArrayList);
                startActivity(intent);

            }
        });

        //

        //크루 있는 유저

        TextView crewName = rootview.findViewById(R.id.crewYesCrewName);
        crewImg = rootview.findViewById(R.id.crewYesCrewImage);
        TextView crewUserNum = rootview.findViewById(R.id.crewYesCrewUserNum);
        TextView crewLoc = rootview.findViewById(R.id.crewYesCrewLoc);
        TextView crewExp = rootview.findViewById(R.id.crewYesCrewExp);
        Button secessionBtn = rootview.findViewById(R.id.secessionBtn);
        databaseReferenceUser = database.getReference("UU");
        databaseReferenceCrew = database.getReference("Crew");
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://doubleu-2df72.appspot.com");
        StorageReference storageReference = storage.getReference();


        //유저가 속한 크루 정보 가져오기
        databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("currentCrew").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(currentCrew.equals("none")){}
                else{
                    databaseReferenceCrew.child(currentCrew).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            crewObject crewInfo = snapshot.getValue(crewObject.class);

                            getCrewName = crewInfo.getCrewName();
                            getCrewUserNum = String.valueOf(crewInfo.getTotalUserNum());
                            getCrewLoc = crewInfo.getLocation();
                            getCrewExp = crewInfo.getExplanation();

                            crewName.setText(getCrewName);
                            crewUserNum.setText(getCrewUserNum);
                            crewLoc.setText(getCrewLoc);
                            crewExp.setText(getCrewExp);
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    crewYesImg = storageReference.child("crew/" + currentCrew + ".png");
                    crewYesImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(rootview)
                                    .load(uri)
                                    .into(crewImg);
                        }
                    });
                    crewYesImg.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            recallCrewImgFromStorage(crewYesImg);
                        }
                    });


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //mCallback.fail(error.getMessage());
            }
        });

        //크루 탈퇴 버튼

        secessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReferenceCrew.child(currentCrew).child("totalUserNum").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        crewNum = snapshot.getValue(Integer.class);
                        databaseReferenceCrew.child(currentCrew).child("totalUserNum").setValue(crewNum-1);                       //크루 인원 한명 제거

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //HashMap<String, Object> updates = new HashMap<>();
                //updates.put(firebaseUser.getUid(), FieldValue.delete());
                FancyToast.makeText(rootview.getContext(),"Success to secession",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                databaseReferenceCrew.child(currentCrew).child("userList").child(firebaseUser.getUid()).removeValue();              //유저 정보 크루에서 제거

                databaseReferenceUser.child("UserAccount").child(firebaseUser.getUid()).child("currentCrew").setValue("none");      //크루 none 상태로 변경

                crewAddedListener.OnCrewAdded();
            }
        });


        //크루원, 크루일정 viewpager 코딩

        TabLayout tabLayout = (TabLayout) rootview.findViewById(R.id.tabLayout);
        ViewPager2 crewPager = (ViewPager2) rootview.findViewById(R.id.crewPager);
        CrewPagerAdapter crewPagerAdapter;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        crewPagerAdapter = new CrewPagerAdapter(fm, getLifecycle());

        crewPager.setAdapter(crewPagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("크루원"));
        tabLayout.addTab(tabLayout.newTab().setText("크루일정"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                crewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        crewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        return rootview;
    }


    public void layoutConverter(int which_layout){
        if(which_layout==R.id.show_crew){
            if(currentCrew.equals("none")){
                detail_name.setText("Crew list");
                show_crew.setBackgroundResource(R.drawable.ic_crew_selected);
                show_lounge.setVisibility(View.INVISIBLE);
                linear_crew_no.setVisibility(View.VISIBLE);
                linear_crew_yes.setVisibility(View.INVISIBLE);
            }
            else {
                detail_name.setText(currentCrew+" Home");
                show_crew.setBackgroundResource(R.drawable.ic_crew_selected);
                show_lounge.setBackgroundResource(R.drawable.ic_lounge);
                linear_crew_no.setVisibility(View.INVISIBLE);
                linear_crew_yes.setVisibility(View.VISIBLE);
            }
        }
        else if(which_layout==R.id.show_lounge){
            Intent intent = new Intent(getContext(), LoungeActivity.class);
            intent.putExtra("whatKindOfLounge", 0);
            intent.putExtra("LoungeID", getCrewName);
            startActivity(intent);

        }

    }

    public void recallCrewImgFromStorage(StorageReference crewYesImg){
        crewYesImg.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                recallCrewImgFromStorage(crewYesImg);
            }
        });
        crewYesImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(rootview)
                        .load(uri)
                        .into(crewImg);
            }
        });
    }



}
