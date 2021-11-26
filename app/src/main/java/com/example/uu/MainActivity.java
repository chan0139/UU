package com.example.uu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends AppCompatActivity  implements customDialog.OnScheduleCreatedListener ,fragment_login.OnLogInCompleteListener, crewAddDialog.OnCrewAddedListener, crewAdapter.OnCrewAddedListener, fragment_crew.OnCrewAddedListener {
    private static final String API_KEY="AIzaSyCtR1gj33Jv0oDKpb7PyHVYlXXJsFRp_KQ";
    private GeoApiContext mGeoApiContext=null;

    Toolbar toolbar;
    TextView title;
    Fragment selectedFragment = null;

    BottomNavigationView bottomNavigationView;
    private boolean isRunning = false;

    // for db
    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    private Uri mapUri;
    private String startAddress;
    private String endAddress;
    private String address;
    private String getDistance;
    private List<com.example.uu.LatLng> checkpoint=new ArrayList<>();

    private String recruitToken;

    //for notification
    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;
    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    //for access firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefRecruit;
    ArrayList<String> userRecruitList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        if(mGeoApiContext==null){
            mGeoApiContext=new GeoApiContext.Builder().apiKey(API_KEY).build();
        }

        dbHelper = new DatabaseHelper(this);

        //for notification
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //toolbar를 찾아 인프레이션하고 actionbar로 변경(actionbar가 기능이 많음)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //의문점 actionbar로 바꿈으로써 actionbar의 기능을 사용하는가?
        //그렇지 않다면 굳이 actionbar로 바꾸지말고 그냥 toolbar의 view들을 따로 인플레이션해서 사용하는건 어떤가

        //appbar 이름 view
        title = (TextView) findViewById(R.id.title);

        title.setText("Login");
        fragment_login fragment_login = new fragment_login();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_login).commit();




        ImageButton profile = (ImageButton) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("Profile");
                selectedFragment = new bar_profile();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        });

        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("Settings");
                selectedFragment = new bar_settings();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        hideNavigationBar();
        if(user == null){
            return;
        }
        else {
            DeleteFinishedRecruit();
            setAlarm();
        }

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

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.crew:
                            title.setText("Crew");
                            selectedFragment = new fragment_crew(R.id.show_crew);
                            break;
                        case R.id.recruitment:
                            title.setText("Recruitment");
                            selectedFragment = new fragment_recruitment(R.id.show_recruitment);
                            break;
                        case R.id.running:
                            title.setText("Running");
                            selectedFragment = new fragment_running();
                            break;
                        case R.id.record:
                            title.setText("Record");
                            selectedFragment = new fragment_record();
                            break;
                    }

                    // 운동 중 화면 전환 발생시 대화상자를 통해 알림
                    if (isRunning && (item.getItemId() == R.id.running))
                        return true;
                    else if (isRunning) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                        dlg.setTitle("열심히 달리는 중인데요!");
                        dlg.setMessage("운동을 종료하고 다른 화면으로 이동할까요?");

                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "운동 종료!", Toast.LENGTH_SHORT).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                        });
                        dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bottomNavigationView.getMenu().findItem(R.id.running).setChecked(true);     // 화면전환 취소되면 메뉴 바를 다시 러닝 화면으로 교체
                            }
                        });
                        dlg.show();
                    } else        //운동 중이 아닐때는 바로 화면 전환
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

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
        title.setText("Crew");
        showCrewFragment();
    }

    public void showCrewFragment() {
        selectedFragment = new fragment_crew(R.id.show_crew);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    public void showRecruitmentFragment() {
        selectedFragment = new fragment_recruitment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }


    @Override
    public void OnCrewAdded() {
        selectedFragment = new fragment_crew(R.id.show_crew);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }


    public void setRunningState(boolean state) {
        isRunning = state;
    }


    // record to db if running ends

    public void recordRunningState(String date, int distance, int time, float calories, int startTime, String runningDay, Address startAddress, Address endAddress)
    {
        // on local DB
        //only record actual running data
        if (distance != 0) {
            sqLiteDb = dbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.PRIMARY_KEY, date);
            values.put(DatabaseHelper.RUNNING_DISTANCE, distance);
            values.put(DatabaseHelper.RUNNING_TIME, time);
            values.put(DatabaseHelper.CONSUMED_CALORIES, calories);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = sqLiteDb.insert(DatabaseHelper.TABLE_NAME, null, values);
            if (newRowId == -1)
                Log.e("DB Error", "data insertion error");
            else
                Log.d("DB Record", "db 저장 완료" + date);
        }

        //on FireBase

        // date는 시작 시간 yy:mm:dd:hh:mm:ss
        // distance는 총 운동 거리 (m단위)
        // time은 총 운동 시간, 분 단위
        //calories, 소비 칼로리
        //startTime, 시작 시간. 24시 기준으로 몇 시 인지만 정수로 반환 (ex. 16시 32분 시작 -> startTime==16)
        // runningDay는 운동한 요일(Mon~Sun), 한글로 바꾸고 싶으면 fragment_running에서 수정
        // start/endAddress는 시작/끝 위치
        //위경도를 알고싶으면
        startAddress.getLatitude(); startAddress.getLongitude();
        //위치를 알고싶으면( ~동 기준)
        startAddress.getThoroughfare();
    }


    public void OnScheduleCreated(String scheduleToken, recruit_object recruitObject) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://doubleu-2df72.appspot.com");
        StorageReference getstorageReference = storage.getReference();
        StorageReference recruitImg = getstorageReference.child("recruitment/" + scheduleToken + ".png");

        recruitObject.setMapUrl("https://firebasestorage.googleapis.com/v0/b/doubleu-2df72.appspot.com/o/recruitment%2F" + recruitImg.getName() + "?alt=media");

        recruitObject.setCheckpoint(this.checkpoint);
        recruitObject.setOrigin(startAddress);
        recruitObject.setDestination(endAddress);
        recruitObject.setAddress(address);
        recruitObject.setDistance(getDistance);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recruit");
        databaseReference.child(scheduleToken).setValue(recruitObject);
        showRecruitmentFragment();
        hideNavigationBar();
    }

    @Override
    public void OnDrawingAcitivyPressed(String recruitToken) {
        this.recruitToken = recruitToken;
        Intent intent = new Intent(this, DrawingMapActivity.class);
        startActivityForResult(intent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            mapUri = data.getParcelableExtra("mapUri");
            StorageReference setstorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference riverRef = setstorageReference.child("recruitment/" + recruitToken + ".png");
            UploadTask uploadTask = riverRef.putFile(mapUri);

            checkpoint = data.<com.example.uu.LatLng>getParcelableArrayListExtra("checkpoint");
            address = data.getStringExtra("address");
            getDistance = data.getStringExtra("distance");
            startAddress=data.getStringExtra("startAddress");
            //Log.d("Tlqkf",startAddress+"");
            endAddress=data.getExtras().getString("endAddress");
            //Log.d("Tlqkf",endAddress+"");
        }
    }

    private void setAlarm() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM.dd HH:mm");
        String getTime = sdf.format(date);
        //Log.e("date", getTime);

        //AlarmReceiver에 값 전달
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");
        mDatabaseRefRecruit = FirebaseDatabase.getInstance().getReference("Recruit");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRecruitList = new ArrayList<>();

        mDatabaseRef.child("UserAccount").child(user.getUid()).child("recruitList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRecruitList.clear();
                for (DataSnapshot snapshotNode: snapshot.getChildren()) {
                    String getUserRecruit = (String) snapshotNode.getKey();
                    userRecruitList.add(getUserRecruit);
                }
                mDatabaseRefRecruit.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot Snapshot : snapshot.getChildren()) {
                            recruit_object recruit = Snapshot.getValue(recruit_object.class);

                            recruit.getDate();

                            Intent receiverIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, receiverIntent, 0);


                            String from = "2021-" + recruit.getDate() + " " + recruit.getAlarmTime();
                            //Log.e("fromdate", from);
                            //Log.e("getdate", getTime);
                            if(from.compareTo(getTime) < 0){

                                return;
                            }
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM.dd HH:mm");

                            Date datetime = null;
                            try {
                                datetime = dateFormat.parse(from);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(datetime);

                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);//임의로 날짜와 시간을 지정

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void DeleteFinishedRecruit(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        String getTime = sdf.format(date);

        //Log.e("date", getTime);

        database = FirebaseDatabase.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");
        mDatabaseRefRecruit = FirebaseDatabase.getInstance().getReference("Recruit");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRecruitList = new ArrayList<>();

        //Delete recruitList
        mDatabaseRef.child("UserAccount").child(user.getUid()).child("recruitList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRecruitList.clear();
                for (DataSnapshot snapshotNode: snapshot.getChildren()) {
                    String getUserRecruit = (String) snapshotNode.getKey();
                    userRecruitList.add(getUserRecruit);
                }
                mDatabaseRefRecruit.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot Snapshot : snapshot.getChildren()) {
                            recruit_object recruit = Snapshot.getValue(recruit_object.class);
                            for(int i = 0; i < userRecruitList.size(); i++){
                                if(userRecruitList.get(i).equals(recruit.getRecruitId())){
                                    if(recruit.getDate().compareTo(getTime) < 0){
                                        mDatabaseRef.child("UserAccount").child(user.getUid()).child("recruitList").setValue(null);
                                    }
                                }
                            }
                            if(recruit.getDate().compareTo(getTime) < 0){
                                mDatabaseRefRecruit.child(recruit.getRecruitId()).setValue(null);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

