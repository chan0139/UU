package com.example.uu;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class bar_profile extends Fragment {

    private String getNickname, getProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.bar_profile,container,false);




        if(getArguments() != null) {
            getNickname = getArguments().getString("name");
            getProfile = getArguments().getString("profileImg");
            TextView ninkname = rootview.findViewById(R.id.ninkname);
            ImageView profile = rootview.findViewById(R.id.profile);

            ninkname.setText(getNickname); // 닉네임 설정
            Glide.with(this).load(getProfile).into(profile); //프로필 이미지 사진 설정
        }
        else Toast.makeText(rootview.getContext(), "fail to get user info", Toast.LENGTH_SHORT).show();

        Button button = rootview.findViewById(R.id.logoutButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        ((MainActivity)getActivity()).replaceFragment(fragment_login.newInstance());
                    }
                });
            }
        });

        return rootview;
    }
}
