package com.example.uu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PersonalRecentDataAdapter extends RecyclerView.Adapter<PersonalRecentDataAdapter.CustomViewHolder> {
    private ArrayList<RecentData>arrayList;

    public PersonalRecentDataAdapter(ArrayList<RecentData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PersonalRecentDataAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recentrecord,parent,false);
       PersonalRecentDataAdapter.CustomViewHolder holder=new PersonalRecentDataAdapter.CustomViewHolder(view);
       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalRecentDataAdapter.CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getRouteUrl())
                .into(holder.routeImg);
        holder.date.setText(arrayList.get(position).getDate());
        holder.loaction.setText(arrayList.get(position).getLocation());
        holder.distance.setText(arrayList.get(position).getDistance()+" m");
        holder.time.setText(arrayList.get(position).getTime()+" 분");
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends  RecyclerView.ViewHolder{

        ImageView routeImg;
        TextView date;
        TextView loaction;
        TextView distance;
        TextView time;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            routeImg=(ImageView) itemView.findViewById(R.id.routeImg);
            date=(TextView) itemView.findViewById(R.id.date);
            loaction=(TextView) itemView.findViewById(R.id.location);
            distance=(TextView) itemView.findViewById(R.id.distance);
            time=(TextView) itemView.findViewById(R.id.time);
        }
    }
}
