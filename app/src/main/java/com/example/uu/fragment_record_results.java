package com.example.uu;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_record_results#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_record_results extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_record_results() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_record_results.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_record_results newInstance(String param1, String param2) {
        fragment_record_results fragment = new fragment_record_results();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private int show_what;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_record_results, container, false);

        show_what=1;
        CircularProgressView personalAchievement=(CircularProgressView) rootView.findViewById(R.id.personalAchievement);
        personalAchievement.setText("gi");
        personalAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(show_what==1){
                    personalAchievement.setText("gi");
                    show_what=0;
                }
                else{
                    personalAchievement.setText(personalAchievement.getProgress()+"%");
                    show_what=1;
                }
            }
        });

        //detail --> https://weeklycoding.com/mpandroidchart-documentation/getting-started/
        HorizontalBarChart mBarchart=(HorizontalBarChart)rootView.findViewById(R.id.id_horizontal_barchart);
        //prepare data
        ArrayList<BarEntry> barEntries=new ArrayList<>();
        barEntries.add(new BarEntry(1f,30));
        barEntries.add(new BarEntry(2f,40));
        barEntries.add(new BarEntry(3f,50));
        barEntries.add(new BarEntry(4f,40));
        barEntries.add(new BarEntry(5f,30));
        barEntries.add(new BarEntry(6f,70));
        barEntries.add(new BarEntry(7f,50));
        //to add the values in X-axis
        ArrayList<String> xAxisName= new ArrayList<>();
        xAxisName.add("Monday");
        xAxisName.add("Tuesday");
        xAxisName.add("Wednesday");
        xAxisName.add("Thursday");
        xAxisName.add("Friday");
        xAxisName.add("Saturday");
        xAxisName.add("Sunday");

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
        BarDataSet barDataSet = new BarDataSet(arrayList, "Label");
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
}