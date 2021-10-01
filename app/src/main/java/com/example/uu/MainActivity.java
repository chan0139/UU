package com.example.uu;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView title;
    Fragment selectedFragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,LoadingActivity.class);
        startActivity(intent);

        //toolbar를 찾아 인프레이션하고 actionbar로 변경(actionbar가 기능이 많음)
        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //appbar 이름 view
        title=(TextView) findViewById(R.id.title);

        title.setText("Recruitment");
        selectedFragment=new fragment_recruitment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

        ImageButton profile=(ImageButton)findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("Profile");
                selectedFragment=new bar_profile();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
        });

        ImageButton settings=(ImageButton)findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("Settings");
                selectedFragment=new bar_settings();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
        });


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        hideNavigationBar();
    }
    private void hideNavigationBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.recruitment:
                            title.setText("Recruitment");
                            selectedFragment=new fragment_recruitment();
                            break;
                        case R.id.running:
                            title.setText("Running");
                            selectedFragment=new fragment_running();
                            break;
                        case R.id.record:
                            title.setText("Record");
                            selectedFragment=new fragment_record();
                            break;
                        case R.id.ranking:
                            title.setText("Ranking");
                            selectedFragment=new fragment_ranking();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                    return true;
                }
            };
}