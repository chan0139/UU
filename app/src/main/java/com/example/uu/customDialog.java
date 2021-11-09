package com.example.uu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;




import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;


public class customDialog extends Dialog {
    int selectedYear=0, selectedMonth=0, selectedDay=0, selectedHour=0, selectedMin=0, getUserNum;
    String getLeader;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    String selectedSpeed;
    String[] runningType = {"평보","경보","달리기"};
    private Activity activity;
    Context context;

    public customDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    public OnScheduleCreatedListener scheduleCreatedListener;
    interface OnScheduleCreatedListener{
        void OnSecheduleCreated();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scheduleCreatedListener=(OnScheduleCreatedListener)context;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Recruit");

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_recruit);
        setCancelable(true);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, runningType);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpeed = runningType[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        Button dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        Button timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime();
            }
        });

        Button setMap=findViewById(R.id.setMap);
        setMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,DrawingMapActivity.class);
                context.startActivity(intent);
            }
        });

        Button addButton = findViewById(R.id.yesBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이 버튼 누르면 DB에 모집정보 저장

                EditText editTextLeader = (EditText)findViewById(R.id.editTextLeader);
                if ( editTextLeader.getText().toString().length() == 0 ) {
                    Toast.makeText(getContext(), "Input Leader Information.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    getLeader = editTextLeader.getText().toString();
                }

                EditText editTextUserNum = (EditText)findViewById(R.id.editTextUserNum);
                if ( editTextUserNum.getText().toString().length() == 0 ) {
                    Toast.makeText(getContext(), "Input UserNumber Information.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    getUserNum = Integer.parseInt(editTextUserNum.getText().toString());
                }
                saveRecruitInfo();

                //FragmentTransaction tr = activity.getFragmentManager().beginTransaction();
                //fragment_recruitment recruitment = new fragment_recruitment();
                //tr.detach(this).attach(this).commit();
                //tr.detach(recruitment);


                scheduleCreatedListener.OnSecheduleCreated();

                dismiss();


            }
        });

        Button cancelButton = findViewById(R.id.noBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }

        });
    }

    void showDate() {
        Calendar today = Calendar.getInstance();
        int currentY = today.get(Calendar.YEAR);
        int currentM = today.get(Calendar.MONTH);
        int currentD = today.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month+1;

                selectedDay = dayOfMonth;
                if(selectedMonth*selectedDay!=0){
                    TextView setDate=findViewById(R.id.setdate);
                    setDate.setText(selectedMonth+" 월 "+selectedDay+" 일   ");
                }

            }
        },currentY, currentM, currentD);
        datePickerDialog.show();
    }

    void showTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMin = minute;
                if(selectedHour*selectedMin!=0){
                    TextView setDate=findViewById(R.id.settime);
                    setDate.setText(selectedHour+" 시 "+selectedMin+" 분   ");
                }            }
        }, 14, 00, true);
        timePickerDialog.show();
    }

    void saveRecruitInfo(){
        String date;
        String time;
        String randomStr = RandomGenerator();
        String castDay;
        if(0< selectedDay && selectedDay <10){
            castDay = '0' + Integer.toString(selectedDay);
        }
        else castDay = Integer.toString(selectedDay);
        time = Integer.toString(selectedHour) + ':' + Integer.toString(selectedMin);
        date = Integer.toString(selectedMonth) +'.' + castDay;
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        recruit_object recruit = new recruit_object();
        recruit.setMapUrl("https://firebasestorage.googleapis.com/v0/b/doubleu-2df72.appspot.com/o/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C.png?alt=media&token=d3eeb566-1e8b-48f3-9c25-d27191bf43ad");
        recruit.setDate(date);
        recruit.setTime(time);
        recruit.setLeader(getLeader);
        recruit.setTotalUserNum(getUserNum);
        recruit.setCurrentUserNum(1);
        recruit.setRunningSpeed(selectedSpeed);
        recruit.setRecruitId(randomStr);
        recruit.setHostId(firebaseUser.getUid());
        mDatabaseRef.child(randomStr).setValue(recruit);
        //Toast.makeText(getContext(), "Success to save in DB", Toast.LENGTH_SHORT).show();
    }

    String RandomGenerator(){               //recruitId 생성기
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }
    return new String(temp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scheduleCreatedListener.OnSecheduleCreated();
        dismiss();
    }
}



