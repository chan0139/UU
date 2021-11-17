package com.example.uu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FitTestStepFirstFragment extends Fragment {
    ViewGroup rootview;
    String targetCrew_Name;

    FitTestStepFirstFragment(String targetCrew_Name){
        this.targetCrew_Name=targetCrew_Name;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = (ViewGroup) inflater.inflate(R.layout.fragment_fittest_stepfirst,container,false);

        TextView targetCrewName=rootview.findViewById(R.id.targetCrewName);
        targetCrewName.setText(targetCrew_Name);

        return rootview;
    }
}
