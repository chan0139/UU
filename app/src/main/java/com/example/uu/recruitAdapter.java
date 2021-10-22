package com.example.uu;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class recruitAdapter extends RecyclerView.Adapter<recruitAdapter.CustomViewHolder> {
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;

    private ArrayList<recruit_object> arrayList;
    private Context context;

    public recruitAdapter(ArrayList<recruit_object> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recruitment, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

            return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {  //각 item 매칭
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getMapUrl())
                .into(holder.testImage);
        holder.date.setText(arrayList.get(position).getDate());
        holder.leader.setText(arrayList.get(position).getLeader());
        holder.currentUserNum.setText(String.valueOf(arrayList.get(position).getCurrentUserNum()));
        holder.totalUserNum.setText(String.valueOf(arrayList.get(position).getTotalUserNum()));

        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                database = FirebaseDatabase.getInstance();
                mDatabaseRef = database.getReference("Recruit");

                int index = arrayList.get(position).getDate().indexOf("/");                 //date 인덱싱
                String day = arrayList.get(position).getDate().substring(index+1);

                holder.currentUserNum.setText(String.valueOf(arrayList.get(position).getCurrentUserNum() + 1));         // 화면에 보이는 현재인원 + 1
                mDatabaseRef.child(arrayList.get(position).getRecruitId()).child("currentUserNum").setValue(arrayList.get(position).getCurrentUserNum() + 1); // DB에 현재인원 추가
                Map<String, Object> addUser = new HashMap<String, Object>();
                addUser.put(firebaseUser.getUid(), "add");
                mDatabaseRef.child(arrayList.get(position).getRecruitId()).child("users").updateChildren(addUser); // DB에 현재인원 추가

            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView testImage;
        TextView date;
        TextView leader;
        TextView currentUserNum;
        TextView totalUserNum;
        Button joinButton;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.testImage = itemView.findViewById(R.id.testImage);
            this.date = itemView.findViewById(R.id.date);
            this.leader = itemView.findViewById(R.id.leader);
            this.currentUserNum = itemView.findViewById(R.id.currentUserNum);
            this.totalUserNum = itemView.findViewById(R.id.totalUserNum);
            this.joinButton = itemView.findViewById(R.id.joinButton);

        }
    }



}
