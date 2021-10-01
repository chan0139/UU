package com.example.uu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.MapView;

public class fragment_running extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview=(ViewGroup) inflater.inflate(R.layout.fragment_running,container,false);

        MapView mapView = new MapView((MainActivity)getActivity());

        ViewGroup mapViewContainer = (ViewGroup)rootview.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        return rootview;
    }
}
