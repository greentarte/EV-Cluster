package com.example.student.phone;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView title, range, status, temp, location, testbattery;
    ImageView battery, doorlock, air;
    int door_status = 0;
    int air_status = 0;
    int battery_percent = 0;
    boolean flag = true;

    //DB to App data
    String updateUrl = "http://70.12.114.147/ws/control_get.do";
    String set_temp = "";
    String available_distance = "";
    String battery_capacity = "";
    String indoor_temp = "";
    String address = "";

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.menu);
        range = findViewById(R.id.range);
        temp = findViewById(R.id.temp);
        location = findViewById(R.id.location);
        battery = findViewById(R.id.batteryview);
        testbattery = findViewById(R.id.testbattery);
        doorlock = findViewById(R.id.doorlock);
        air = findViewById(R.id.air);
        battery = findViewById(R.id.batteryview);



        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //spring에서 값을 받아와야함
                                //필요한 데이터
                                // 배터리용량, 주행가능거리, 실내온도, 위치, 사용자 정보
                                UpdateTask updateTask = new UpdateTask();
                                updateTask.execute();

                            }
                        });

                        Thread.sleep(5000); // 1 분마다


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    public void showBattery() {

        if (battery_percent >= 90) {
            battery.setImageResource(R.drawable.b90);
        } else if (battery_percent >= 80) {
            battery.setImageResource(R.drawable.b80);
        } else if (battery_percent >= 70) {
            battery.setImageResource(R.drawable.b70);
        } else if (battery_percent >= 60) {
            battery.setImageResource(R.drawable.b60);
        } else if (battery_percent >= 50) {
            battery.setImageResource(R.drawable.b50);
        } else if (battery_percent >= 40) {
            battery.setImageResource(R.drawable.b40);
        } else if (battery_percent >= 30) {
            battery.setImageResource(R.drawable.b30);
        } else if (battery_percent >= 20) {
            battery.setImageResource(R.drawable.b20);
        } else if (battery_percent >= 10) {
            battery.setImageResource(R.drawable.b10);
        } else if (battery_percent <= 0) {
            battery.setImageResource(R.drawable.b0);
        }
    }


    //팬모양 클릭시 변화는것
    public void air(View v) {

        //작동시 이미지 색깔 변화필요
        if (air_status == 0) {
            air_status = 1; //on으로 변경
            Toast.makeText(this, "사용자 설정온도 ON", Toast.LENGTH_SHORT).show();
            air.setImageResource(R.drawable.fan);
        } else {
            air_status = 0; //off로 변경
            Toast.makeText(this, "사용자 설정온도 OFF", Toast.LENGTH_SHORT).show();
            air.setImageResource(R.drawable.wind);
        }

    }

    //프로필 클릭시 변하는 것
    public void profile(View v) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        flag=false;
    }

    //자물쇠 모양 클릭시 변하는 것
    public void doorlock(View v) {
        //DB에서 차량의 상태를 받아와서 변수에 저장

        if (door_status == 1) {
            door_status = 0; //잠금으로 변경
            Toast.makeText(this, "LOCK", Toast.LENGTH_SHORT).show();
            doorlock.setImageResource(R.drawable.lock);
        } else {
            door_status = 1;
            Toast.makeText(this, "UNLOCK", Toast.LENGTH_SHORT).show();
            doorlock.setImageResource(R.drawable.unlock);
        }
    }


    //온도조절 클릭시 온도제어 화면으로 전환
    public void control_temp(View v) {
        Intent intent = new Intent(MainActivity.this, TempActivity.class);
        startActivity(intent);
        flag=false;
    }

    //충전 클릭시 충전제어 화면으로 전환
    public void control_charge(View v) {
        Intent intent = new Intent(MainActivity.this, ChargeActivity.class);
        startActivity(intent);
        flag=false;

    }

    //위치 클릭시 위치 화면으로 전환
    public void display_location(View v) {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
        flag=false;
    }

    class UpdateTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean result = false;
            URL url = null;
            HttpURLConnection con = null;
            BufferedReader br = null;
            try {
                url = new URL("http://70.12.114.147/ws/control_get.do");

                con = (HttpURLConnection) url.openConnection();
                if (con != null) {
                    con.setConnectTimeout(5000);
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept", "*/*");

                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String[] outResult = br.readLine().toString().split("/");

                    set_temp = outResult[0];
                    available_distance = outResult[3];
                    battery_percent = (int) (Double.parseDouble(outResult[4]) / 90 * 100);
                    indoor_temp = outResult[5];
                    address = outResult[10];
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //UI change 기입
            showBattery();
            range.setText(available_distance);
            temp.setText(indoor_temp);
            testbattery.setText("" + battery_percent);
            location.setText(address);

//                    status, temp, location, testbattery;
        }
    }

    @Override
    protected void onDestroy() {
        flag=false;

        super.onDestroy();
    }
}

