package com.example.uu;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.style.TtsSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.util.exception.KakaoException;
import com.kiprotich.japheth.TextAnim;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;




public class fragment_login extends Fragment{
    private ISessionCallback mSessionCallback;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    private  OnLogInCompleteListener logInCompleteListener;
    interface OnLogInCompleteListener{
        void hideNavigation();
        void loginComplete();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        logInCompleteListener=(OnLogInCompleteListener) context;
    }

    public static fragment_login newInstance() {
        return new fragment_login();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_login,container,false);

        logInCompleteListener.hideNavigation();
        ImageView runGif = (ImageView) rootview.findViewById(R.id.loginGif);
        Glide.with(this).load(R.raw.mov_location).into(runGif);

        TextAnim textAnimation;
        TextAnim textAnimation2;

        textAnimation = rootview.findViewById(R.id.textAnimation);
        textAnimation2 = rootview.findViewById(R.id.textAnimation2);


            textAnimation
                    .setWidth(12)
                    .setDelay(30)
                    .setColor(Color.DKGRAY)
                    .setConfig(TextAnim.Configuration.INTERMEDIATE)
                    .setSizeFactor(30f)
                    .setLetterSpacing(25f)
                    .setText("RUN WITH U")
                    .setListener(new TextAnim.Listener() {
                        @Override
                        public void WritingFinished() {
                            //
                        }
                    })
                    .startAnimation();


            textAnimation2
                    .setWidth(12)
                    .setDelay(30)
                    .setColor(Color.DKGRAY)
                    .setConfig(TextAnim.Configuration.INTERMEDIATE)
                    .setSizeFactor(30f)
                    .setLetterSpacing(25f)
                    .setText("SHARE WITH U")
                    .setListener(new TextAnim.Listener() {
                        @Override
                        public void WritingFinished() {
                            //
                        }
                    })
                    .startAnimation();


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");


        //로그인 관리
        mSessionCallback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                //로그인 요청
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        //로그인 실패
                        Toast.makeText(rootview.getContext(), "Failed to login.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        //세션이 닫힌 경우
                        Toast.makeText(rootview.getContext(), "Session is closed. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {  //MeV2Response result에 프로필 정보 등 요구했던 정보들이 담겨 있음.
                        //로그인 성공
//                        Intent intent = new Intent(rootview.getContext(), MainActivity.class);  //회원정보 여기서 메인 액으로 넘기고,, 받는거 생각
//                        intent.putExtra("name", result.getKakaoAccount().getProfile().getNickname());
//                        intent.putExtra("profileImg", result.getKakaoAccount().getProfile().getProfileImageUrl());

                        String name = result.getKakaoAccount().getProfile().getNickname();
                        String gender = String.valueOf(result.getKakaoAccount().getGender());
                        String urlLink = result.getKakaoAccount().getProfile().getProfileImageUrl();
                        String email = result.getKakaoAccount().getEmail();
                        String crew = "none";

                        String defaultPwd = "wh21dasfgr124!@";      //firebaseauth 이용 위해서는 필수, 그러나 우리는 카카오 아이디로 하기때문에 그냥 임시값으로 저장..
                        mFirebaseAuth.createUserWithEmailAndPassword(email, defaultPwd).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    userObject user = new userObject();
                                    user.setUserName(name);
                                    user.setUserGender(gender);
                                    user.setUserId(email);
                                    user.setDefaultPwd(defaultPwd);
                                    user.setIdToken(firebaseUser.getUid());
                                    user.setUserProfileUrl(urlLink);
                                    user.setCurrentCrew(crew);
                                    user.setUserLevel(1);
                                    user.setUserRecruitJoinNumber(0);

                                    //setValue -> db에 insert
                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(user);
                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("FitTest").setValue(initFitTestData());
                                    //Toast.makeText(rootview.getContext(), "Success to save in DB", Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    //Toast.makeText(rootview.getContext(), "Fail to save in DB", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        mFirebaseAuth.signInWithEmailAndPassword(email, defaultPwd).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //Toast.makeText(rootview.getContext(), "Success to login", Toast.LENGTH_SHORT).show();
                                    logInCompleteListener.loginComplete();

                                }
                            }
                        });


                        //Toast.makeText(rootview.getContext(), "Success to Login", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {

            }
        };
        Session.getCurrentSession().addCallback(mSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();   //로그아웃 안하면 로그인 상태 유지


        getHashKey();

        return rootview;
    }

    // get hashkey method, 카카오 로그인시 필요한 해쉬키 얻는 함수
    // 컴퓨터마다 다르므로 test 위해선 각자 hashkey developer에 추가 필요
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mSessionCallback);
    }

    private void updateUI(FirebaseUser user) {

    }
    public FitTestData initFitTestData(){
        FitTestData mfitTestData=new FitTestData();
        mfitTestData.setNumberOfRunning(0);
        mfitTestData.setCrewName("Personal");
        mfitTestData.setRunningTime(0);
        mfitTestData.setDistance(0);
        List<Integer> day=new ArrayList<>();
        List<Integer> starttime=new ArrayList<>();
        while(starttime.size()!=24){
            starttime.add(0);
            if(day.size()<7){
                day.add(0);
            }
        }
        mfitTestData.setDay(day);
        mfitTestData.setStartTime(starttime);
        List<com.example.uu.LatLng> start=new ArrayList<>();
        LatLng temp=new LatLng();
        temp.setLatitude((double) 0);
        temp.setLongitude((double) 0);
        start.add(temp);
        mfitTestData.setStartAddress(start);
        List<com.example.uu.LatLng> end=new ArrayList<>();
        end.add(temp);
        mfitTestData.setEndAddress(end);
        return mfitTestData;
    }
}

