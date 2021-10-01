package com.example.uu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;


public class fragment_login extends Fragment{
    private ISessionCallback mSessionCallback;

    public static fragment_login newInstance() {
        return new fragment_login();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_login,container,false);

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
                        String urlLink = result.getKakaoAccount().getProfile().getProfileImageUrl();

                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        bundle.putString("profileImg", urlLink);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bar_profile profile = new bar_profile();
                        profile.setArguments(bundle);
                        ((MainActivity)getActivity()).replaceFragment(profile);
                        transaction.commit();
                        Toast.makeText(rootview.getContext(), "Success to Login", Toast.LENGTH_SHORT).show();
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
}

