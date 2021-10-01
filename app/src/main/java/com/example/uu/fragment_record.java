package com.example.uu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fragment_record extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_record,container,false);

        ImageButton show_record=(ImageButton) rootview.findViewById(R.id.show_record);
        ImageButton show_trophy=(ImageButton) rootview.findViewById(R.id.show_trophy);

        show_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_record.setBackgroundResource(R.drawable.ic_record_selected);
                show_trophy.setBackgroundResource(R.drawable.ic_trophy);
            }
        });

        show_trophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_record.setBackgroundResource(R.drawable.ic_record);
                show_trophy.setBackgroundResource(R.drawable.ic_trophy_selected);
            }
        });


        return rootview;
    }
}
