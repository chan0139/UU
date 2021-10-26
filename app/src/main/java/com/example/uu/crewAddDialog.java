package com.example.uu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class crewAddDialog extends Dialog {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    String selectedGu;
    String getCrewName;
    String getCrewExp;

    public crewAddDialog(@NonNull Context context) {
        super(context);
    }

    public crewAddDialog.OnScheduleCreatedListener scheduleCreatedListener;
    interface OnScheduleCreatedListener{
        void OnScheduleCreated();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Crew");

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_crew_add);
        setCancelable(true);

        ImageView crewImage = findViewById(R.id.crewImage);
        /*
        crewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         */

        Spinner spinner = findViewById(R.id.crewAddSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.seoul_gu, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGu = (String) adapterView.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Button createBtn = findViewById(R.id.addCrewBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이 버튼 누르면 DB에 모집정보 저장

                EditText editTextCrewName = (EditText)findViewById(R.id.editTextCrewName);
                if ( editTextCrewName.getText().toString().length() == 0 ) {
                    Toast.makeText(getContext(), "Input Crew Name.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    getCrewName = editTextCrewName.getText().toString();
                }

                EditText editTextCrewExp = (EditText)findViewById(R.id.editTextCrewExp);
                if ( editTextCrewExp.getText().toString().length() == 0 ) {
                    Toast.makeText(getContext(), "Input Crew Explanation", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    getCrewExp = editTextCrewExp.getText().toString();
                }

                saveCrewInfo();

                //scheduleCreatedListener.OnScheduleCreated();

                dismiss();

            }
        });

        Button cancelButton = findViewById(R.id.cancelCrewBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }

        });


    }


    void saveCrewInfo(){

        Map<String, Object> addUser = new HashMap<String, Object>();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        crewObject crew = new crewObject();
        crew.setCrewImage("https://firebasestorage.googleapis.com/v0/b/doubleu-2df72.appspot.com/o/default_crew_image.PNG?alt=media&token=9e2bb55a-759a-471a-83f6-e19cc1201bc9");
        crew.setCrewName(getCrewName);
        crew.setExplanation(getCrewExp);
        crew.setLeader(firebaseUser.getUid());
        crew.setTotalUserNum(1);
        crew.setLocation(selectedGu);
        addUser.put(firebaseUser.getUid(), "id");
        crew.setUserList(addUser);
        mDatabaseRef.child(getCrewName).setValue(crew);
        Toast.makeText(getContext(), "Success to save in DB", Toast.LENGTH_SHORT).show();
    }
}
