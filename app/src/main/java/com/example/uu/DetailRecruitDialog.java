package com.example.uu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private int width;
    private int height;


    @Override
    public void onResume() {
        super.onResume();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point deviceSize = new Point();
        display.getSize(deviceSize);
        width = deviceSize.x;
        height = (int) (deviceSize.y *(0.9));


        getDialog().getWindow().setLayout(width,height);
    }

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
        TextView textDistance = (TextView) rootview.findViewById(R.id.rootDistance);
        TextView textAddress = rootview.findViewById(R.id.address);


        Bundle bundle = getArguments();
        String getLeader = bundle.getString("leader", "leader");
        String getTime = bundle.getString("time", "time");
        String getDate = bundle.getString("date", "date");
        String getMap = bundle.getString("mapUrl", "mapUrl");
        String getSpeed = bundle.getString("speed", "speed");
        String getCur = bundle.getString("curuser", "cur");
        String getTot = bundle.getString("totuser", "tot");
        String getAddress = bundle.getString("address", "address");
        String getDistance = bundle.getString("distance", "distance");


        textLeader.setText(getLeader);
        textTime.setText(getTime);
        textDate.setText(getDate);
        Glide.with(getContext()).load(getMap).into(textMap);
        textSpeed.setText(getSpeed);
        textCur.setText(getCur);
        textTot.setText(getTot);
        textAddress.setText(getAddress);
        textDistance.setText(getDistance);


        Button cancelButton = rootview.findViewById(R.id.closeButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return rootview;
    }

    public Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return  size;
    }


}

