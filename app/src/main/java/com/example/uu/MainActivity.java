package com.example.uu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity  implements customDialog.OnScheduleCreatedListener,fragment_login.OnLogInCompleteListener, crewAddDialog.OnCrewAddedListener, crewAdapter.OnCrewAddedListener, fragment_recruitment.OnCrewAddedListener {

    Toolbar toolbar;
    TextView title;
    Fragment selectedFragment=null;

    BottomNavigationView bottomNavigationView;
    private boolean isRunning=false;

    // for db
    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    private Uri mapUri;
    private String recruitToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,LoadingActivity.class);
        startActivity(intent);

        dbHelper=new DatabaseHelper(this);


        //toolbar를 찾아 인프레이션하고 actionbar로 변경(actionbar가 기능이 많음)
        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //의문점 actionbar로 바꿈으로써 actionbar의 기능을 사용하는가?
        //그렇지 않다면 굳이 actionbar로 바꾸지말고 그냥 toolbar의 view들을 따로 인플레이션해서 사용하는건 어떤가

        //appbar 이름 view
        title=(TextView) findViewById(R.id.title);
        title.setText("Login");
        fragment_login fragment_login = new fragment_login();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_login).commit();



        ImageButton profile=(ImageButton)findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("Profile");
                selectedFragment=new bar_profile();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
        });

        ImageButton settings=(ImageButton)findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("Settings");
                selectedFragment=new bar_settings();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
        });


        bottomNavigationView=findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        hideNavigationBar();

    }


    private void hideNavigationBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.recruitment:
                            title.setText("Recruitment");
                            selectedFragment=new fragment_recruitment(R.id.show_recruitment);
                            break;
                        case R.id.running:
                            title.setText("Running");
                            selectedFragment=new fragment_running();
                            break;
                        case R.id.record:
                            title.setText("Record");
                            selectedFragment=new fragment_record();
                            break;
                        case R.id.ranking:
                            title.setText("Ranking");
                            selectedFragment=new fragment_ranking();
                            break;
                    }
                    
                    // 운동 중 화면 전환 발생시 대화상자를 통해 알림
                    if(isRunning&&(item.getItemId()==R.id.running))
                        return true;
                    else if(isRunning) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                        dlg.setTitle("열심히 달리는 중인데요!");
                        dlg.setMessage("운동을 종료하고 다른 화면으로 이동할까요?");

                        dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this,"운동 종료!",Toast.LENGTH_SHORT).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                            }
                        });
                        dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bottomNavigationView.getMenu().findItem(R.id.running).setChecked(true);     // 화면전환 취소되면 메뉴 바를 다시 러닝 화면으로 교체
                            }
                        });
                        dlg.show();
                    }
                    else        //운동 중이 아닐때는 바로 화면 전환
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                    return true;
                }
            };

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }



    @Override
    public void loginComplete() {
        title.setText("Recruitment");
        showRecruitmentFragment();
    }
    public void showRecruitmentFragment(){
        selectedFragment=new fragment_recruitment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
    }

  
    @Override
    public void OnCrewAdded(){
        selectedFragment= new fragment_recruitment(R.id.show_crew);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
    }



     public void setRunningState(boolean state){
        isRunning=state;
    }


    // record to db if running ends
    public void recordRunningState(String date,int distance,int time,float calories)
    {
        //only record actual running data
        if(distance!=0) {
            sqLiteDb = dbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.PRIMARY_KEY, date);
            values.put(DatabaseHelper.RUNNING_DISTANCE, distance);
            values.put(DatabaseHelper.RUNNING_TIME, time);
            values.put(DatabaseHelper.CONSUMED_CALORIES, calories);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = sqLiteDb.insert(DatabaseHelper.TABLE_NAME, null, values);
            if(newRowId==-1)
                Log.e("DB Error","data insertion error");
            else
                Log.d("DB Record","db 저장 완료"+date);
        }
    }





    public void OnScheduleCreated(String scheduleToken,recruit_object recruitObject) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://doubleu-2df72.appspot.com");
        StorageReference getstorageReference = storage.getReference();
        StorageReference recruitImg =getstorageReference.child("recruitment/" + scheduleToken + ".png");

        recruitObject.setMapUrl("https://firebasestorage.googleapis.com/v0/b/doubleu-2df72.appspot.com/o/recruitment%2F"+recruitImg.getName()+"?alt=media");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recruit");
        databaseReference.child(scheduleToken).setValue(recruitObject);
        showRecruitmentFragment();
        hideNavigationBar();
    }

    @Override
    public void OnDrawingAcitivyPressed(String recruitToken) {
        this.recruitToken=recruitToken;
        Intent intent = new Intent(this,DrawingMapActivity.class);
        startActivityForResult(intent,999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==999 && resultCode== Activity.RESULT_OK){
            mapUri = data.getParcelableExtra("mapUri");
            StorageReference setstorageReference= FirebaseStorage.getInstance().getReference();
            StorageReference riverRef = setstorageReference.child("recruitment/"+recruitToken+".png");
            UploadTask uploadTask= riverRef.putFile(mapUri);

        }
    }
}

