package com.example.uu;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class fragment_recruitment extends Fragment {
    private View linear_recruitment;
    private View linear_crew;
    private LinearLayout linear_dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_recruitment,container,false);

        Button recruit=(Button)rootview.findViewById(R.id.recruit);
        recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog dialog = new customDialog(getActivity());

                dialog.show();
            }
        });

        linear_recruitment=(LinearLayout)rootview.findViewById(R.id.linear_Recruitment);
        linear_crew=(LinearLayout)rootview.findViewById(R.id.linear_crew);
        linear_crew.setVisibility(View.INVISIBLE);

        ImageButton show_recruitment=(ImageButton) rootview.findViewById(R.id.show_recruitment);
        ImageButton show_crew=(ImageButton) rootview.findViewById(R.id.show_crew);

        show_recruitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_crew.setBackgroundResource(R.drawable.ic_crew);
                linear_recruitment.setVisibility(View.VISIBLE);
                linear_crew.setVisibility(View.INVISIBLE);
            }
        });

        show_crew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_crew.setBackgroundResource(R.drawable.ic_crew_selected);
                linear_recruitment.setVisibility(View.INVISIBLE);
                linear_crew.setVisibility(View.VISIBLE);
            }
        });

        return rootview;
    }
}
