package com.example.uu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class crewUserAdapter extends RecyclerView.Adapter<crewUserAdapter.CustomViewHolder>{
    private ArrayList<userObject> arrayList;

    public crewUserAdapter(ArrayList<userObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public crewUserAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_crew_user, parent, false);
        crewUserAdapter.CustomViewHolder holder = new crewUserAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull crewUserAdapter.CustomViewHolder holder, int position) {

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getUserProfileUrl())
                .into(holder.crewYesUserImg);
        holder.crewYesName.setText(arrayList.get(position).getUserName());
        String level = String.valueOf(arrayList.get(position).getUserLevel());
        holder.crewYesLevel.setText("Level " + level);
        holder.crewYesRole.setText(arrayList.get(position).getCrewRole());
        //holder.crewYesRole.setText(arrayList.get(position).getLeader());
        //holder.crewYesLevel.setText(String.valueOf(arrayList.get(position).getCurrentUserNum()));

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView crewYesUserImg;
        TextView crewYesName;
        TextView crewYesRole;
        TextView crewYesLevel;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.crewYesUserImg = itemView.findViewById(R.id.crewYesUserImg);
            this.crewYesName = itemView.findViewById(R.id.crewYesName);
            this.crewYesRole = itemView.findViewById(R.id.crewYesRole);
            this.crewYesLevel = itemView.findViewById(R.id.crewYesLevel);

        }
    }
}
