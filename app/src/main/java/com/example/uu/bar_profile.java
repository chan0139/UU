package com.example.uu;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.w3c.dom.Text;

public class bar_profile extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private String userId, userProfileUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.bar_profile,container,false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");
        String uid = user != null ? user.getUid() : null;
        TextView nickname = rootview.findViewById(R.id.ninkname);
        ImageView profile = rootview.findViewById(R.id.profile);

        mDatabaseRef.child("UserAccount").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userObject info = snapshot.getValue(userObject.class);
                userId = info.getUserId();
                userProfileUrl = info.getUserProfileUrl();
                nickname.setText(userId);
                Glide.with(getContext()).load(userProfileUrl).into(profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        



        //DatabaseReference id = mDatabase.child("UserAccount").child(uid).child("userId");



        Button button = rootview.findViewById(R.id.logoutButton);
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
            }
        });

        return rootview;
    }
}
