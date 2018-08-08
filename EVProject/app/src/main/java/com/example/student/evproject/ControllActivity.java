package com.example.student.evproject;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class ControllActivity extends AppCompatActivity {

    TabLayout ctrlTab;
    ViewPager ctrlPager;
    FloatingActionButton back_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controll);
        //상태바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ctrlTab = findViewById(R.id.tabs);
        ctrlPager = findViewById(R.id.ctrlPager);
        back_bt = findViewById(R.id.back_bt);

        ctrlTab.setupWithViewPager(ctrlPager);

        ControlPagerAdapter adapter = new ControlPagerAdapter(getSupportFragmentManager());
        ctrlPager.setAdapter(adapter);

        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        }

}
