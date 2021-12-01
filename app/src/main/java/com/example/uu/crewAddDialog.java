package com.example.uu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class crewAddDialog extends DialogFragment {
    public static String TAG="dialog_crew_add";

    public OnCrewAddedListener crewAddedListener;
    interface OnCrewAddedListener{
        void  OnCrewAdded();
    }

    ImageView clubImg;
    FloatingActionButton fab;
    Uri uri;
    String selectedGu;
    String getCrewName;
    String getCrewExp;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefUser;
    private FirebaseStorage storage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        crewAddedListener=(OnCrewAddedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.dialog_crew_add, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Crew");
        mDatabaseRefUser = FirebaseDatabase.getInstance().getReference("UU");
        storage = FirebaseStorage.getInstance();

        clubImg = rootview.findViewById(R.id.clubImg);
        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        uri = result.getData().getData();
                        clubImg.setImageURI(uri);

                        // Use the uri to load the image
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error

                    }
                });


        fab = rootview.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(requireActivity())
                        .crop()
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        }));
            }
        });

        Spinner spinner = rootview.findViewById(R.id.crewAddSpinner);
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


        Button createBtn = rootview.findViewById(R.id.addCrewBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이 버튼 누르면 DB에 모집정보 저장

                EditText editTextCrewName = (EditText)rootview.findViewById(R.id.editTextCrewName);
                if ( editTextCrewName.getText().toString().length() == 0 ) {
                    Toast.makeText(getContext(), "Input Crew Name.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    getCrewName = editTextCrewName.getText().toString();
                }

                EditText editTextCrewExp = (EditText)rootview.findViewById(R.id.editTextCrewExp);
                if ( editTextCrewExp.getText().toString().length() == 0 ) {
                    Toast.makeText(getContext(), "Input Crew Explanation", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    getCrewExp = editTextCrewExp.getText().toString();
                }

                saveCrewInfo();


                crewAddedListener.OnCrewAdded();

                dismiss();

            }
        });

        Button cancelButton = rootview.findViewById(R.id.cancelCrewBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }

        });


        return rootview;


    }

    void saveCrewInfo(){

        //저장소에 크루 사진 정보 저장..
        StorageReference storageReference = storage.getReference();
        StorageReference riversRef = storageReference.child("crew/" + getCrewName + ".png");
        if(uri != null) {
            UploadTask uploadTask = riversRef.putFile(uri);
        }


        //DB에 크루 정보 저장
        Map<String, Object> addUser = new HashMap<String, Object>();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        crewObject crew = new crewObject();
        crew.setCrewImage(String.valueOf(uri));
        crew.setCrewName(getCrewName);
        crew.setExplanation(getCrewExp);
        crew.setLeader(firebaseUser.getUid());
        crew.setTotalUserNum(1);
        crew.setLocation(selectedGu);
        addUser.put(firebaseUser.getUid(), "id");
        crew.setUserList(addUser);
        mDatabaseRef.child(getCrewName).setValue(crew);
        mDatabaseRef.child(getCrewName).child("FitTest").setValue(initFitTestData());
        mDatabaseRefUser.child("UserAccount").child(firebaseUser.getUid()).child("currentCrew").setValue(getCrewName);
        mDatabaseRefUser.child("UserAccount").child(firebaseUser.getUid()).child("crewRole").setValue("Leader");
        Toast.makeText(getContext(), "Success to save in DB", Toast.LENGTH_SHORT).show();
    }

    public FitTestData initFitTestData(){
        FitTestData mfitTestData=new FitTestData();
        mfitTestData.setNumberOfRunning(0);
        mfitTestData.setCrewName(getCrewName);
        mfitTestData.setRunningTime(0);
        mfitTestData.setDistance(0);
        List<Integer> day=new ArrayList<>();
        List<Integer> starttime=new ArrayList<>();
        while(starttime.size()!=24){
            starttime.add(0);
            if(day.size()<7){
                day.add(0);
            }
        }
        mfitTestData.setDay(day);
        mfitTestData.setStartTime(starttime);
        List<com.example.uu.LatLng> start=new ArrayList<>();
        LatLng temp=new LatLng();
        temp.setLongitude((double) 0);
        temp.setLatitude((double) 0);
        start.add(temp);
        mfitTestData.setStartAddress(start);
        List<com.example.uu.LatLng> end=new ArrayList<>();
        end.add(temp);
        mfitTestData.setEndAddress(end);
        return mfitTestData;
    }


}
