package com.example.uu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class customDialog extends Dialog {
    int selectedYear=0, selectedMonth=0, selectedDay=0, selectedHour=0, selectedMin=0;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    String selectedSpeed;
    String[] runningType = {"평보","경보","달리기"};

    public customDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UU");

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

        Button addButton = findViewById(R.id.yesBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 날짜, 시간 잘 받아오는지 test용
                Toast.makeText(getContext(), selectedYear+"."+selectedMonth+"."+selectedDay+"\n"+selectedHour+":" + selectedMin, Toast.LENGTH_SHORT).show();
            }
        });

        Button cancelButton = findViewById(R.id.noBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
            }
        }, 14, 00, true);
        timePickerDialog.show();
    }
}




