package com.example.student.evproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ChargingActivity extends AppCompatActivity {

    Handler handler = new Handler();
    int value = 0;
    int add = 1;
    RadioGroup radioGroup;
    RadioButton onRc, offRc;
    TextView txtTime;
    int mHour, mMinute;
    String amPm;
    String url = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        //상태바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        radioGroup = findViewById(R.id.radioGroup);

        txtTime=findViewById(R.id.txtTime);
        onRc = findViewById(R.id.on);
        offRc = findViewById(R.id.off);

        final Calendar cal = new GregorianCalendar();
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        UpdateNow();

        if(offRc.isChecked()){
            txtTime.setTextColor(Color.parseColor("#aaaaaa"));
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.on){
                    onRc.setTextColor(Color.parseColor("#000000"));
                    offRc.setTextColor(Color.parseColor("#ffffff"));
                    txtTime.setTextColor(Color.parseColor("#ffffff"));
                    txtTime.setClickable(true);
                    txtTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new TimePickerDialog(ChargingActivity.this,
                                    AlertDialog.THEME_HOLO_DARK,mTimeSetListener,mHour,mMinute,false).show();
                        }
                    });
                }else if(i == R.id.off){
                    onRc.setTextColor(Color.parseColor("#ffffff"));
                    offRc.setTextColor(Color.parseColor("#000000"));
                    txtTime.setTextColor(Color.parseColor("#aaaaaa"));
                    mHour = cal.get(Calendar.HOUR_OF_DAY);
                    mMinute = cal.get(Calendar.MINUTE);
                    UpdateNow();
                    txtTime.setClickable(false);
                }

            }
        });



        final ProgressBar progressBar = findViewById(R.id.progressBar1);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() { // Thread 로 작업할 내용을 구현
                while(true) {
                    value = value + add;
                    if (value>=100 || value<=0) {
                        add = -add;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() { // 화면에 변경하는 작업을 구현
                            progressBar.setProgress(value);
                        }
                    });

                    try {
                        Thread.sleep(100); // 시간지연
                    } catch (InterruptedException e) {    }
                } // end of while
            }
        });
        t.start();
    }

    public void UpdateNow(){
        if(mHour > 12){
            amPm = "오후";
            mHour = mHour - 12;
        }else{
            amPm = "오전";
        }
        txtTime.setText(String.format("%s %02d : %02d",amPm, mHour,mMinute));
    }

    TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // TODO Auto-generated method stub
                    //사용자가 입력한 값을 가져온뒤
                    mHour = hourOfDay;
                    mMinute = minute;

                    //텍스트뷰의 값을 업데이트함
                    UpdateNow();

                }
            };


    //DB 연동

    Thread chargingThread = new Thread(new Runnable() {
        @Override
        public void run() {
            url = "http://70.12.114.147/ws/charging_insert.do?car_charging_status=charging&car_charging_date=2018-05-21%2014:30:23&car_charging_place=default&car_battery_capacity=20&car_battery_cicle=201&car_battery_temperature=35&car_remain_date=85&car_number=Tesla%20S";
            ChargingUpdateTask chargingUpdateTask = new ChargingUpdateTask(url);
            chargingUpdateTask.execute();
        }
    });






    class ChargingUpdateTask extends AsyncTask<String, Void, String> {

        String url;

        ChargingUpdateTask() {
        }

        ChargingUpdateTask(String url) {
            this.url = url;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            //http request
            StringBuilder sb = new StringBuilder();
            URL url;
            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {
                url = new URL(this.url);
                con = (HttpURLConnection) url.openConnection();


                if (con != null) {
                    con.setConnectTimeout(10000);   //connection 5초이상 길어지면 exepction
                    //con.setReadTimeout(10000);
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept", "*/*");
                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
                        return null;


                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line = null;
                    while (true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }


                }


            } catch (Exception e) {
                return e.getMessage();   //리턴하면 post로
            } finally {

                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                con.disconnect();
            }


            return sb.toString();

        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s.equals("1")) {
                    Toast.makeText(ChargingActivity.this, "입력 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChargingActivity.this, "입력 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    } //UpdateTask
}
