package com.example.uu;

import android.content.Context;
import android.media.Image;
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

public class recruitAdapter extends RecyclerView.Adapter<recruitAdapter.CustomViewHolder> {

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
        //Button joinButton;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.testImage = itemView.findViewById(R.id.testImage);
            this.date = itemView.findViewById(R.id.date);
            this.leader = itemView.findViewById(R.id.leader);
            this.currentUserNum = itemView.findViewById(R.id.currentUserNum);
            this.totalUserNum = itemView.findViewById(R.id.totalUserNum);
            //this.joinButton = itemView.findViewById(R.id.joinButton);
        }
    }
}
