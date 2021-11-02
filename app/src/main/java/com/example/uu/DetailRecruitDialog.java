package com.example.uu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.DialogFragment;


import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailRecruitDialog extends DialogFragment {
    public static String TAG="dialog_detail_recruit";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.dialog_detail_recruit,container,false);
        //추후에 인원정보도 추가..

        TextView textLeader = (TextView) rootview.findViewById(R.id.leader);
        TextView textTime = (TextView) rootview.findViewById(R.id.time);
        TextView textDate = (TextView) rootview.findViewById(R.id.date);
        ImageView textMap = (ImageView) rootview.findViewById(R.id.map);
        TextView textSpeed = (TextView) rootview.findViewById(R.id.runningspeed);
        TextView textCur = (TextView) rootview.findViewById(R.id.curUserNum);
        TextView textTot = (TextView) rootview.findViewById(R.id.totalUserNum);

        Bundle bundle = getArguments();
        String getLeader = bundle.getString("leader", "leader");
        String getTime = bundle.getString("time", "time");
        String getDate = bundle.getString("date", "date");
        String getMap = bundle.getString("map", "map");
        String getSpeed = bundle.getString("speed", "speed");
        String getCur = bundle.getString("curuser", "cur");
        String getTot = bundle.getString("totuser", "tot");


        textLeader.setText(getLeader);
        textTime.setText(getTime);
        textDate.setText(getDate);
        Glide.with(getContext()).load(getMap).into(textMap);
        textSpeed.setText(getSpeed);
        textCur.setText(getCur);
        textTot.setText(getTot);


        Button cancelButton = rootview.findViewById(R.id.closeButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return rootview;
    }


}


/*
    Intent intent;

    private Context context;
    public DetailRecruitDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_detail_recruit);

        TextView textLeader = findViewById(R.id.leader);

        intent = new Intent(getContext(), fragment_recruitment.class);
        String leader = intent.getExtras().getString("leader");
        textLeader.setText(leader);
        Button cancelButton = findViewById(R.id.closeButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }

        });

    }

 */