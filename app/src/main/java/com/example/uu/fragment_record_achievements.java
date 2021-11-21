package com.example.uu;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Semaphore;

public class fragment_record_achievements extends Fragment {

    GridView gridView;

    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    // data for checking achievement complete
    private int total_distance=0;
    private int max_distance=0;
    private int total_time=0;
    private int max_time=0;
    private int total_recruit_time=0;
    private int level;

    //region flag variable for checking each achievements
    private boolean flag_maxDistance_3=false;
    private boolean flag_maxDistance_5=false;
    private boolean flag_maxDistance_10=false;
    private boolean flag_totalDistance_10=false;
    private boolean flag_totalDistance_50=false;
    private boolean flag_totalDistance_100=false;
    private boolean flag_maxTime_20=false;
    private boolean flag_maxTime_40=false;
    private boolean flag_maxTime_60=false;
    private boolean flag_totalTime_300=false;
    private boolean flag_totalTime_500=false;
    private boolean flag_totalTime_1000=false;
    private boolean flag_recruitJoinTime_1=false;
    private boolean flag_recruitJoinTime_5=false;
    private boolean flag_recruitJoinTime_10=false;

    private ProgressBar levelBar;
    private TextView levelPercentText;
    private ImageView levelImage;
    private TextView levelText;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private Integer getUserRecruitJoinNumber;
    private achievementAdapter adapter;
    private Integer getNum;

    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_record_achievements, container, false);
        gridView=(GridView) rootView.findViewById(R.id.achievementContainer);
        database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference("UU");
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        adapter=new achievementAdapter();

        level = 1;

        levelBar = (ProgressBar) rootView.findViewById(R.id.levelProgressBar);
        levelImage = rootView.findViewById(R.id.levelImage);
        levelText = rootView.findViewById(R.id.levelText);
        levelPercentText = rootView.findViewById(R.id.levelPercentText);


        readData(databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("userRecruitJoinNumber"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                levelImage.setImageResource(R.drawable.level1);
                levelText.setText("Level 1");
                levelPercentText.setText("0%");
                levelBar.setProgress(0);
                getNum = dataSnapshot.getValue(Integer.class);
                total_recruit_time = getNum;

                getRecordData();
                setGrid();
            }
            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
        
        return rootView;
    }

    private void setGrid(){
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        adapter.addItem(new achievementObject("조린이","한번에 3km 달리기",flag_maxDistance_3));
        adapter.addItem(new achievementObject("프로 마라토너","한번에 5km 달리기",flag_maxDistance_5));
        adapter.addItem(new achievementObject("전생에 말","한번에 10km 달리기",flag_maxDistance_10));

        adapter.addItem(new achievementObject("뛰는게 즐겁다!","총 10km 달리기",flag_totalDistance_10));
        adapter.addItem(new achievementObject("섹시한 말벅지","총 50km 달리기",flag_totalDistance_50));
        adapter.addItem(new achievementObject("꾸준함의 미덕","총 100km 달리기",flag_totalDistance_100));

        adapter.addItem(new achievementObject("어우 숨차..!","20분동안 달리기",flag_maxTime_20));
        adapter.addItem(new achievementObject("나 혹시 철인?","40분동안 달리기",flag_maxTime_40));
        adapter.addItem(new achievementObject("올림픽 나가볼까?","60분동안 달리기",flag_maxTime_60));

        adapter.addItem(new achievementObject("영차 영차","총 달린 시간 300분",flag_totalTime_300));
        adapter.addItem(new achievementObject("살 좀 빠졌나?","총 달린 시간 500분",flag_totalTime_500));
        adapter.addItem(new achievementObject("난 달린다\n고로 존재한다","총 달린 시간 1000분",flag_totalTime_1000));

        adapter.addItem(new achievementObject("함께 뛰는 즐거움!","러닝메이트 모집 1회",flag_recruitJoinTime_1));
        adapter.addItem(new achievementObject("이제 나도 인싸?","러닝메이트 모집 5회",flag_recruitJoinTime_5));
        adapter.addItem(new achievementObject("러닝메이트 부자","러닝메이트 모집 10회",flag_recruitJoinTime_10));
        gridView.setAdapter(adapter);
        //adpter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                achievementObject item=(achievementObject) adapter.getItem(i);

                // 해당 아이템 클릭시, 상세정보 표시 ( 제목, 내용, 달성률, 달성 일자)
                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                dlg.setTitle(item.getObjName());
                dlg.setMessage(item.getDescription()+"\n달성률 : "+item.getAchievement()+"\n달성 일자 : "+item.getClearDate());
                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dlg.show();
            }
        });

        updateLevelImg();
        databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("userLevel").setValue(level);

    }

    private void getRecordData()
    {
        dbHelper=new DatabaseHelper(getContext());
        sqLiteDb=dbHelper.getReadableDatabase();


        // Query
        String queryDistanceSum="SELECT SUM("+DatabaseHelper.RUNNING_DISTANCE+") FROM "+DatabaseHelper.TABLE_NAME+";";
        String queryDistanceMax="SELECT MAX("+DatabaseHelper.RUNNING_DISTANCE+") FROM "+DatabaseHelper.TABLE_NAME+";";
        String queryTimeSum="SELECT SUM("+DatabaseHelper.RUNNING_TIME+") FROM "+DatabaseHelper.TABLE_NAME+";";
        String queryTimeMax="SELECT MAX("+DatabaseHelper.RUNNING_TIME+") FROM "+DatabaseHelper.TABLE_NAME+";";

        Cursor cursor;

        cursor = sqLiteDb.rawQuery(queryDistanceSum,null);
        cursor.moveToFirst();
        total_distance=cursor.getInt(0);

        cursor = sqLiteDb.rawQuery(queryDistanceMax,null);
        cursor.moveToFirst();
        max_distance=cursor.getInt(0);

        cursor = sqLiteDb.rawQuery(queryTimeSum,null);
        cursor.moveToFirst();
        total_time=cursor.getInt(0);

        cursor = sqLiteDb.rawQuery(queryTimeMax,null);
        cursor.moveToFirst();
        max_time=cursor.getInt(0);

        cursor.close();
        sqLiteDb.close();

        setFlags();
    }

    private void setFlags()
    {
        if(max_distance>3) {
            flag_maxDistance_3 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(max_distance>5) {
            flag_maxDistance_5 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }
        if(max_distance>10) {
            flag_maxDistance_10 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_distance>10) {
            flag_totalDistance_10 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_distance>50) {
            flag_totalDistance_50 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_distance>100) {
            flag_totalDistance_100 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(max_time>20) {
            flag_maxTime_20 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(max_time>40) {
            flag_maxTime_40 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(max_time>60) {
            flag_maxTime_60 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_time>300) {
            flag_totalTime_300 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_time>500) {
            flag_totalTime_500 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_time>1000) {
            flag_totalTime_1000 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_recruit_time>=1) {
            flag_recruitJoinTime_1 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_recruit_time>=5) {
            flag_recruitJoinTime_5 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }

        if(total_recruit_time>=10) {
            flag_recruitJoinTime_10 = true;
            levelBar.incrementProgressBy(20);
            checkLevelBar();
        }


        levelPercentText.setText(levelBar.getProgress() + "%");
    }
    private void checkLevelBar(){
        int progress = levelBar.getProgress();

        if(progress == 100){
            levelBar.setProgress(0);
            level += 1;
            levelText.setText("Level " + level);
        }

    }
    private void updateLevelImg(){
        switch (level){
            case 2: levelImage.setImageResource(R.drawable.level2);
                break;
            case 3: levelImage.setImageResource(R.drawable.level3);
                break;
            case 4: levelImage.setImageResource(R.drawable.level4);
                break;
            case 5: levelImage.setImageResource(R.drawable.level5);
                break;
        }
    }

    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }
        });
    }
}