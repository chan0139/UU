package com.example.uu;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class fragment_record_achievements extends Fragment {

    GridView gridView;

    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    // data for checking achievement complete
    private int total_distance=0;
    private int max_distance=0;
    private int total_time=0;
    private int max_time=0;

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
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_record_achievements, container, false);

        getRecordData();    // DB에서 필요한 정보들 먼저 받아와서
        setFlags();         // 트로피 표시여부 플래그 설정

        gridView=(GridView) rootView.findViewById(R.id.achievementContainer);
        achievementAdapter adapter=new achievementAdapter();

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

        gridView.setAdapter(adapter);
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

        return rootView;
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
    }

    private void setFlags()
    {
        if(max_distance>3)
            flag_maxDistance_3=true;

        if(max_distance>5)
            flag_maxDistance_5=true;

        if(max_distance>10)
            flag_maxDistance_10=true;

        if(total_distance>10)
            flag_totalDistance_10=true;

        if(total_distance>50)
            flag_totalDistance_50=true;

        if(total_distance>100)
            flag_totalDistance_100=true;

        if(max_time>20)
            flag_maxTime_20=true;

        if(max_time>40)
            flag_maxTime_40=true;

        if(max_time>60)
            flag_maxTime_60=true;

        if(total_time>300)
            flag_totalTime_300=true;

        if(total_time>500)
            flag_totalTime_300=true;

        if(total_time>1000)
            flag_totalTime_300=true;
    }
}