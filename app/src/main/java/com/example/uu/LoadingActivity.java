package com.example.uu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        startLoading();
        initView();
    }
    private void startLoading(){

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },3000);
    }
    private void initView(){
        TextView run=(TextView) findViewById(R.id.run);
        TextView share=(TextView) findViewById(R.id.share);
        TextView runwith=(TextView) findViewById(R.id.runwith);
        TextView sharewith=(TextView) findViewById(R.id.sharewith);

        Animation loading_run = AnimationUtils.loadAnimation(this,R.anim.runwithu);
        Animation loading_share=AnimationUtils.loadAnimation(this,R.anim.sharewithu);
        Animation loading_alpha=AnimationUtils.loadAnimation(this,R.anim.loading_alpha);

        run.setAnimation(loading_run);
        runwith.setAnimation(loading_alpha);
        share.setAnimation(loading_share);
        sharewith.setAnimation(loading_alpha);

    }
}