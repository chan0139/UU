package com.example.uu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.owl93.dpb.CircularProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class fragment_record_results extends Fragment {

    // db init
    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    //data list
    ArrayList<BarEntry> barEntries=new ArrayList<>();
    ArrayList<String> xAxisName= new ArrayList<>();


    private int show_what;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_record_results, container, false);

        //init
        dbHelper=new DatabaseHelper(getContext());
        sqLiteDb=dbHelper.getReadableDatabase();


        //CircularProgressView
        show_what=1;
        CircularProgressView personalAchievement=(CircularProgressView) rootView.findViewById(R.id.personalAchievement);
        personalAchievement.setText("gi");
        personalAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(show_what==1){
                    personalAchievement.setText("목표 달성 업적을\n 선택해주세요!");
                    show_what=0;
                }
                else{
                    personalAchievement.setText(personalAchievement.getProgress()+"%");
                    show_what=1;
                }
            }
        });


        //HorizontalBarChart
        //detail --> https://weeklycoding.com/mpandroidchart-documentation/getting-started/
        HorizontalBarChart mBarchart=(HorizontalBarChart)rootView.findViewById(R.id.id_horizontal_barchart);
        //prepare data
        getRecordData();

        barchart(mBarchart,barEntries,xAxisName);

        return rootView;
    }

    public static void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues) {
        barChart.setDrawBarShadow(false);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(25);
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(true);
        BarDataSet barDataSet = new BarDataSet(arrayList, "M(미터)");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        barData.setValueTextSize(0f);

        barChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
        barChart.setDrawGridBackground(false);

        Legend l = barChart.getLegend(); // Customize the legends
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setTextSize(10f);
        l.setFormSize(10f);

//To set components of x axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);

        barChart.setFitBars(true);

        barChart.setData(barData);
        barChart.invalidate();//refresh

    }

    private void getRecordData()
    {
        Cursor cursor;

        String day;
        int distance;

        xAxisName.add("temp");

        // Query
        for(int i=-6;i<1;i++){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE , i);
            String targetDate = new java.text.SimpleDateFormat("yyyy:MM:dd%").format(calendar.getTime());
            String queryDistanceSum="SELECT SUM("+DatabaseHelper.RUNNING_DISTANCE+") FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.PRIMARY_KEY+" LIKE '"+targetDate+"';";

            cursor = sqLiteDb.rawQuery(queryDistanceSum,null);
            cursor.moveToFirst();
            distance=cursor.getInt(0);
            day=getDateDay(calendar.get(Calendar.DAY_OF_WEEK));

            Log.d("day",targetDate+distance);

            barEntries.add(new BarEntry(i+7,distance+5));
            xAxisName.add(day);
        }


        sqLiteDb.close();
    }

    public static String getDateDay(int dayNum)  {
        String day;

        switch (dayNum) {
            case 1:
                day = "Sun";
                break;
            case 2:
                day = "Mon";
                break;
            case 3:
                day = "Tue";
                break;
            case 4:
                day = "Wed";
                break;
            case 5:
                day = "Thu";
                break;
            case 6:
                day = "Fri";
                break;
            case 7:
                day = "Sat";
                break;
            default:
                day=null;

        }

        return day;
    }


}