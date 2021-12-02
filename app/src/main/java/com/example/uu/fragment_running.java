package com.example.uu;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class fragment_running extends Fragment
        implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private String whoseRecord="Personal";
    private String hostId="Personal";;

    public static fragment_running newInstance() {
        return new fragment_running();
    }


    private GoogleMap mMap;

    //show current location
    private static final String TAG = "UU";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private LocationManager locationManager;
    private List<LatLng> checkpoints=new ArrayList<>();
    private List<LatLng> reservedCheckpoints=new ArrayList<>();
    private LatLng currentPosition;


    private boolean walkState = false;                    //걸음 상태
    private int runningTime=0;
    private int distance=0;
    private float calories=0;
    private String formatedNow;
    private String day;
    private int startTime=0;
    private Location startAddress;
    private Location endAddress;

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소



    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요

    //personal recruitment obj
    private HashMap<String,recruit_object> recruitObject=new HashMap<>();
    //현재 참가중인 러닝 key
    private HashMap<Integer,String> runningKey=new HashMap<>();
    private List<recruit_object> nearSchedule=new ArrayList<>();

    //Firebase realtime DB
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get data from timer when running ends
        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                runningTime = bundle.getInt("bundleKey");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //현재 참가중인 running list 불러오기
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("UU");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //running의 key값 불러오기
        mDatabaseRef.child("UserAccount").child(user.getUid()).child("recruitList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runningKey.clear();
                int size=0;
                for (DataSnapshot snapshotNode: snapshot.getChildren()) {
                    String getUserRecruit = (String) snapshotNode.getKey();
                    runningKey.put(size,getUserRecruit);
                    size+=1;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //runnig key값 별로 checkpoint 불러오기
        mDatabaseRef=database.getReference("Recruit");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // DB data를 받아오는곳
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    recruit_object recruit = Snapshot.getValue(recruit_object.class);
                    if(runningKey.containsValue(recruit.getRecruitId())){
                        recruitObject.put(recruit.getRecruitId(),recruit);
                        Log.d("datecheck","hihi");
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DB 받아오던 중 에러 발생하는 경우
                Log.e("Error", String.valueOf(error.toException()));
            }
        });


        //특정 작업시 화면이 꺼지지않게 유지
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //initialize
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_running, container, false);
        mLayout = getActivity().findViewById(R.id.layout_running);

        setMap();


        return rootview;
    }

    private void setMap()
    {
        //init map
        //LocationRequest objects are used to request a quality of service for location updates from the FusedLocationProviderApi.
        //for location request & authentication update
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        //FusedLocationProviderClient  supports various functions that get device location information
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //fetch handle
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //set callback
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Automatically called when google map is available and get googleMap as parameter
        //Get user permission(gps) when map loaded

        mMap = googleMap;

        // Initially set the default location (Seoul,Kor)
        setDefaultLocation();


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신
                        ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로
                // 요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            //Requests location updates with a callback on the specified Looper thread.
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            // mark current position with blue button if permission enabled
            if (mMap != null)
                mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).setRunningState(false);
    }

    /*
        통합 위치 정보 제공자는 LocationCallback.onLocationResult() 콜백 메서드를 호출합니다. 수신 인수에는 위치의 위도 및 경도가 포함되어 있는 목록 Location 객체가 있습니다.
        다음 스니펫은 LocationCallback 인터페이스를 구현하고 메서드를 정의한 후 위치 업데이트의 타임스탬프를 가져오고 앱의 사용자 인터페이스에 위도, 경도 및 타임스탬프를 표시하는 방법을 보여줍니다.
         */
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                // Always update latest location
                location = locationList.get(locationList.size() - 1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

              
                // Add location to drawing buffer if walkState is true(button clicked)
                if(walkState)
                    checkpoints.add(currentPosition);


                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                // match camera pos with current location
                setCurrentLocation(location);
            }
        }
    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }
    }


    public boolean checkLocationServicesStatus() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // make dialog for get location service
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        // check both fine location and course location permission
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    public void setDefaultLocation() {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }

    public void setCurrentLocation(Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }


    //ActivityCompat.requestPermissions 을 사용한 퍼미션 요청의 결과를 리턴
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용 가능
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            getActivity().finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용 가능
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            getActivity().finish();
                        }
                    }).show();
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");

                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


    // For drawing polyline after running finished

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onButtonStart()  {

        checkNearSchedule();

        checkpoints.clear();
        reservedCheckpoints.clear();
        mMap.clear();
        //Toast toast=Toast.makeText(getActivity(),"운동 시작!",FancyToast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER,0,0);
        //toast.show();
        //FancyToast.makeText(getContext(),"운동 시작!",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
        walkState = true;

        //시작 시간 계산, db에 저장할때 기본키로 사용
        LocalDateTime now=LocalDateTime.now();
        formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd-HH:mm:ss"));

        Calendar calendar=Calendar.getInstance();
        startAddress=location;

        startTime=calendar.get(Calendar.HOUR_OF_DAY);
        int dayIndex=calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayIndex){
            case 1:
                day="Sun";
                break;
            case 2:
                day="Mon";
                break;
            case 3:
                day="Tue";
                break;
            case 4:
                day="Wed";
                break;
            case 5:
                day="Thu";
                break;
            case 6:
                day="Fri";
                break;
            case 7:
                day="Sat";
                break;
        }

        ((MainActivity)getActivity()).setRunningState(true);
    }

    public void onButtonPause()
    {
        walkState = false;
    }

    public void onButtonEnd() {
        walkState = false;
        // 액티비티에도 종료 사실 알려주기, 운동이 종료되지 않았는데 화면 전환 시도시 경고 문구 표시
        ((MainActivity)getActivity()).setRunningState(false);

        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        String msg="";

        //end position
        endAddress=location;


        //running time
        String min=Integer.toString((runningTime/100)/60);
        String sec=Integer.toString((runningTime/100)%60);

        //running distance
        float[] results=new float[3];
        for(int i=0;i<checkpoints.size()-1;i++)
        {
            Location.distanceBetween(checkpoints.get(i).latitude,
                    checkpoints.get(i).longitude,
                    checkpoints.get(i+1).latitude,
                    checkpoints.get(i+1).longitude,results);
            distance+=(int)results[0];      // m 단위로 달린 거리 받아오기
        }

        // Kcal calc
        double userWeight=70.0;
        double Met=0.0;
        double time=(runningTime/100)/3600.0;
        double velocity=(distance/1000.0)/time;

        // Differentiate running intensity to make calories calculation more accurate
        if(velocity>=13)
            Met=8.0;
        else if(velocity>=10)
            Met=7.0;
        else if(velocity>=5.5)
            Met=3.6;
        else if(velocity>=4.8)
            Met=3.3;
        else if(velocity>=4)
            Met=2.9;
        else if(velocity>0)
            Met=2.0;

        double Kcal=userWeight*Met*time;
        calories=(float) Math.round((Kcal*10)/10.0);


        // Dialog for running info
        dlg.setTitle("오늘의 운동!"); //제목

        msg="얼마나 달렸을까? "+min+"분 "+sec+"초\n";
        msg+="얼만큼 뛰었을까? "+Integer.toString(distance)+"m\n";
        msg+="뛴만큼 빠졌을까? "+Float.toString(calories)+"Kcal\n";       //소숫점 첫째 자리까지 표현
        dlg.setMessage(msg); // 메시지

        dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                //FancyToast.makeText(getContext(),"운동 종료!",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                drawPath(checkpoints,Color.BLACK);
            }
        });
        dlg.show();

        //write on db
        ((MainActivity)getActivity()).recordRunningState(whoseRecord,hostId,formatedNow,distance,(runningTime/100)/60,calories,startTime,day,startAddress,endAddress);

        formatedNow="";distance=0;calories=0;runningTime=0;endAddress=null;
    }

    private void drawPath(List<LatLng> targetPoints, int color){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().width(15).color(color).geodesic(true);
        Polyline polyline=mMap.addPolyline(options);
        polyline.setPoints(targetPoints);
    }

    // checking for near running schedule from now before starts running, if so, show dialog for user
    public boolean checkNearSchedule(){

        nearSchedule.clear();

        Calendar now = Calendar.getInstance();
        int currentTime=0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmm");
            currentTime=Integer.parseInt(dateFormat.format(now.getTime()));

            for(int i=0;i<recruitObject.size();i++){
                int reservedTime=returnDateFormat(recruitObject.get(runningKey.get(i)).getDate(),recruitObject.get(runningKey.get(i)).getTime());
                if(reservedTime!=-1){
                    // calculate between current time and reserved time, and push if reservation time is near by
                    Log.d("timegap",Math.abs(currentTime-reservedTime)+"");
                    if(Math.abs(currentTime-reservedTime)<=60)
                        nearSchedule.add(recruitObject.get(runningKey.get(i)));
                }
                else{Log.e("time format error","Time format error");}
            }
        }else{Log.d("date format error","API level doesn't match");}


        if(nearSchedule.isEmpty())
            return false;
        else
            return true;

        /*
        쓰는 예시
        recruitObject.get(runningKey.get(i)).getCheckpoint().get(0).getLatitude();
        recruitObject.get(runningKey.get(i)).getCheckpoint().get(0).getLongitude();
        */
    }

    public void showNearSchedule(){

        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        dlg.setTitle("근처에 예약된 러닝 일정이 있네요!");
        dlg.setIcon(R.drawable.ic_runningdlg);
        List<String> scheduleName=new ArrayList<>();
        for(int i=0;i<nearSchedule.size();i++)
            scheduleName.add(nearSchedule.get(i).getDate()+"일 "+nearSchedule.get(i).getTime()+"에 예약된 러닝을 시작할게요!");
        scheduleName.add("모집 일정에 참여하지 않고 달려볼게요~!");

        dlg.setItems(scheduleName.toArray(new String[scheduleName.size()]), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleName.clear();
                RunningTimerFragment timerFragment=(RunningTimerFragment) getChildFragmentManager().findFragmentById(R.id.fragmentContainerView);
                timerFragment.StartTimer();

                Log.d("running",scheduleName.size()+"");
                Log.d("running",which+"");

                if(which!=scheduleName.size()-1) {
                    whoseRecord=nearSchedule.get(which).getLeader();
                    hostId=nearSchedule.get(which).getHostId();
                    for(int i=0;i<nearSchedule.get(which).getCheckpoint().size();i++)
                        reservedCheckpoints.add(new LatLng(nearSchedule.get(which).getCheckpoint().get(i).getLatitude(),nearSchedule.get(which).getCheckpoint().get(i).getLongitude()));
                    drawPath(reservedCheckpoints,Color.RED);
                    reservedCheckpoints.clear();
                }
                else{
                    whoseRecord="Personal";
                }
            }
        });

        dlg.show();
    }

    private int returnDateFormat(String date,String time){
        String returnDate="";
        String returnTime="";

        if(time.length()==4)
            returnTime=time.substring(0,2)+"0"+time.substring(3);
        else if(time.length()==5)
            returnTime=time.substring(0,2)+time.substring(3);
        else
            return -1;

        if(date.length()==4 || time.length()==5)
            returnDate=date.replace(".","");
        else
            return -1;

        return Integer.parseInt(returnDate+returnTime);
    }

}
