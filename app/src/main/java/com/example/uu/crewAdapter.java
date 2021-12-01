package com.example.uu;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
import java.util.HashMap;
import java.util.Map;


public class crewAdapter extends RecyclerView.Adapter<crewAdapter.CustomViewHolder>{
    private ArrayList<crewObject> arrayList;
    private Context context;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefUser;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;
    private String userInCrew;

    public OnCrewListener crewListener;
    interface OnCrewListener{
        void OnCrewAdded();
    }

    public crewAdapter(ArrayList<crewObject> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context=context;
        crewListener = (OnCrewListener) context;
    }

    @NonNull
    @Override
    public crewAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_crew, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull crewAdapter.CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(R.raw.loading)
                .into(holder.crewImage);

        mFirebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://doubleu-2df72.appspot.com");
        StorageReference storageReference = storage.getReference();
        StorageReference crewImg = storageReference.child("crew/" + arrayList.get(position).getCrewName() + ".png");


        crewImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView)
                        .load(uri)
                        .into(holder.crewImage);
            }

        });
        crewImg.getDownloadUrl().addOnFailureListener(new OnFailureListener(){

            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(holder.itemView)
                        .load(R.drawable.ic_sneakers)
                        .into(holder.crewImage);
            }
        });



        holder.crewName.setText(arrayList.get(position).getCrewName());
        holder.crewLoc.setText(arrayList.get(position).getLocation());
        holder.crewUserNum.setText(String.valueOf(arrayList.get(position).getTotalUserNum()));

        holder.crewJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                mDatabaseRef = database.getReference("Crew");
                mDatabaseRefUser = database.getReference("UU");

                holder.crewUserNum.setText(String.valueOf(arrayList.get(position).getTotalUserNum() + 1));// 화면에 보이는 현재인원 + 1

                mDatabaseRef.child(arrayList.get(position).getCrewName()).child("totalUserNum").setValue(arrayList.get(position).getTotalUserNum() + 1); // DB에 현재인원 추가

                Map<String, Object> addUser = new HashMap<String, Object>();
                addUser.put(firebaseUser.getUid(), "id");
                mDatabaseRef.child(arrayList.get(position).getCrewName()).child("userList").updateChildren(addUser); // DB에 현재인원 추가
                mDatabaseRefUser.child("UserAccount").child(firebaseUser.getUid()).child("currentCrew").setValue(arrayList.get(position).getCrewName()); //유저 소속크루 설정
                FancyToast.makeText(context.getApplicationContext(),"Success to join CREW!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                crewListener.OnCrewAdded();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView crewImage;
        TextView crewName;
        TextView crewLoc;
        TextView crewUserNum;
        TextView crewJoinBtn;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.crewImage = itemView.findViewById(R.id.crewImg);
            this.crewName = itemView.findViewById(R.id.crewName);
            this.crewLoc = itemView.findViewById(R.id.crewLocation);
            this.crewUserNum = itemView.findViewById(R.id.crewUserNum);
            this.crewJoinBtn = itemView.findViewById(R.id.joinCrewButton);
        }
    }

}

