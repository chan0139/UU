package com.example.uu;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunningTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningTimerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mStartBtn, mStopBtn, mPauseBtn;
    private TextView mTimeTextView;
    private Thread timeThread = null;
    private Boolean walkState = false;

    fragment_running parentFragment;

    int time = 0;

    public RunningTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunningTimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningTimerFragment newInstance(String param1, String param2) {
        RunningTimerFragment fragment = new RunningTimerFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_running_timer, container, false);

        mStartBtn = (Button) v.findViewById(R.id.Fbtn_start);
        mStopBtn = (Button) v.findViewById(R.id.Fbtn_stop);
        mPauseBtn = (Button) v.findViewById(R.id.Fbtn_pause);
        mTimeTextView = (TextView) v.findViewById(R.id.FtimeView);

        parentFragment=(fragment_running) getParentFragment();

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(!parentFragment.checkNearSchedule()) {
                    v.setVisibility(View.GONE);
                    StartTimer();
                }
                else {
                    v.setVisibility(View.GONE);
                    parentFragment.showNearSchedule();
                }
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                StopTimer();
            }
        });

        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PauseTimer();
            }
        });

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartTimer()
    {

        mTimeTextView.setVisibility(View.VISIBLE);
        mStopBtn.setVisibility(View.VISIBLE);
        mPauseBtn.setVisibility(View.VISIBLE);

        timeThread = new Thread(new timeThread());
        timeThread.start();
        try {
            parentFragment.onButtonStart();
        }catch (Exception e){
            Log.d("FragmentReferenceError","cannot resolve parent fragment");
        }
    }

    public void PauseTimer()
    {
        walkState = !walkState;
        if (!walkState) {
            mPauseBtn.setText("일시정지");
        } else {
            mPauseBtn.setText("시작");
        }
        try {
            parentFragment.onButtonPause();
        }catch (Exception e){
            Log.d("FragmentReferenceError","cannot resolve parent fragment");
        }
    }

    public void StopTimer()
    {
        mTimeTextView.setVisibility(View.GONE);
        mStartBtn.setVisibility(View.VISIBLE);
        mPauseBtn.setVisibility(View.GONE);
        walkState=true;
        timeThread.interrupt();
        walkState=false;
        try {
            Bundle result = new Bundle();
            result.putInt("bundleKey", time);
            getParentFragmentManager().setFragmentResult("requestKey", result);

            parentFragment.onButtonEnd();

            time=0;
        }catch (Exception e){
            Log.d("FragmentReferenceError","cannot resolve parent fragment");
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d", min, sec, mSec);
            mTimeTextView.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            time = 0;

            while (true) {
                while (!walkState) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = time++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                mTimeTextView.setText("");
                                mTimeTextView.setText("00:00:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }


}