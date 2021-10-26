package com.example.uu;

import android.content.Context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class crewAdapter extends RecyclerView.Adapter<crewAdapter.CustomViewHolder> {
    private ArrayList<crewObject> arrayList;
    private Context context;

    public crewAdapter(ArrayList<crewObject> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public crewAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_crew, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull crewAdapter.CustomViewHolder holder, int position) {

        /*
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getCrewImage())
                .into(holder.crewImage);

         */
        holder.crewName.setText(arrayList.get(position).getCrewName());
        holder.crewLoc.setText(arrayList.get(position).getLocation());
        holder.crewUserNum.setText(String.valueOf(arrayList.get(position).getTotalUserNum()));
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

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.crewImage = itemView.findViewById(R.id.crewImage);
            this.crewName = itemView.findViewById(R.id.crewName);
            this.crewLoc = itemView.findViewById(R.id.crewLocation);
            this.crewUserNum = itemView.findViewById(R.id.crewUserNum);



        }
    }
}

