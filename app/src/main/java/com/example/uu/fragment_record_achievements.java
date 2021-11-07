package com.example.uu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class fragment_record_achievements extends Fragment {

    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_record_achievements, container, false);

        gridView=(GridView) rootView.findViewById(R.id.achievementContainer);
        achievementAdapter adapter=new achievementAdapter();

        adapter.addItem(new achievementObject("5km","5km걷기"));
        adapter.addItem(new achievementObject("10km","5km걷기"));
        adapter.addItem(new achievementObject("15km","5km걷기"));
        adapter.addItem(new achievementObject("25km","5km걷기"));
        adapter.addItem(new achievementObject("35km","5km걷기"));
        gridView.setAdapter(adapter);

        return rootView;
    }
}