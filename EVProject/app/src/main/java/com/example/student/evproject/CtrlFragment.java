package com.example.student.evproject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CtrlFragment extends Fragment {
    ImageView wind;
    TextView temp_set, temp_in, temp_out;
    ToggleButton heat_bt, cool_bt;
    ImageButton temp_up, temp_down, w_up, w_down;
    int windP = 0;
    int temp = 23;

    //180601 add code start
    boolean flag = true;
    Handler handler = new Handler();
    int airStatus = 0; //0이면 냉방 1이면 히터
    String indoor_temp;
    //car_control 테이블과 연동시 사용하는 변수
    String set_Temp;
    String set_wind;
    String set_cool;
    String set_warm;
    String set_charging_amount;
    String charging_port;
    String code;


    public CtrlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_ctrl, container, false);

        heat_bt = ll.findViewById(R.id.ctrl_heat);
        cool_bt = ll.findViewById(R.id.ctrl_cool);

        temp_set = ll.findViewById(R.id.temp_set);
        temp_in = ll.findViewById(R.id.temp_in);
        temp_out = ll.findViewById(R.id.temp_out);

        temp_up = ll.findViewById(R.id.bt_temp_up);
        temp_down = ll.findViewById(R.id.bt_temp_down);

        wind = ll.findViewById(R.id.wind);
        w_up = ll.findViewById(R.id.bt_wind_up);
        w_down = ll.findViewById(R.id.bt_wind_down);

        //설정온도 up 클릭이벤트

        temp_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp >= 17 && temp < 32) {
                    temp++;
                }
                temp_set.setText(String.valueOf(temp));
                //DB설정 온도 삽입
                SetTask setTask = new SetTask();
                setTask.execute();
                //DB설정 온도 삽입
            }
        });

        //설정온도 down 클릭이벤트
        temp_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp > 17 && temp <= 32) {
                    temp--;
                }
                temp_set.setText(String.valueOf(temp));
                //DB설정 온도 삽입
                SetTask setTask = new SetTask();
                setTask.execute();
                //DB설정 온도 삽입
            }
        });


        //바람세기 up 버튼 클릭이벤트
        w_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (windP >= 0 && windP < 4) {
                    windP++;
                }
                switch (windP) {
                    case 0:
                        wind.setImageResource(R.drawable.fan);
                        break;
                    case 1:
                        wind.setImageResource(R.drawable.fan_1);
                        break;
                    case 2:
                        wind.setImageResource(R.drawable.fan_2);
                        break;
                    case 3:
                        wind.setImageResource(R.drawable.fan_3);
                        break;
                    case 4:
                        wind.setImageResource(R.drawable.fan_4);
                        break;
                }
            }
        });

        //바람세기 down 버튼 클릭이벤트
        w_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (windP > 0 && windP <= 4) {
                    windP--;
                }
                switch (windP) {
                    case 0:
                        wind.setImageResource(R.drawable.fan);
                        break;
                    case 1:
                        wind.setImageResource(R.drawable.fan_1);
                        break;
                    case 2:
                        wind.setImageResource(R.drawable.fan_2);
                        break;
                    case 3:
                        wind.setImageResource(R.drawable.fan_3);
                        break;
                    case 4:
                        wind.setImageResource(R.drawable.fan_4);
                        break;
                }
            }
        });

        //cool & heat button on/off
        heat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heat_bt.isChecked()) {
                    set_warm = "1";
                    set_cool = "0";
                    heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_on));
                    cool_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.cool_off));
                    wind.setImageResource(R.drawable.tesla_heat);

                } else {
                    set_warm = "0";
                    set_cool = "0";
                    heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_off));
                    wind.setImageResource(R.drawable.tesla_off);
                }
                SetTask setTask = new SetTask();
                setTask.execute();
            }
        });

        cool_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cool_bt.isChecked()) {
                    set_warm = "0";
                    set_cool = "1";
                    cool_bt.setBackgroundDrawable(getContext().getDrawable((R.drawable.cool_on)));
                    heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_off));
                    wind.setImageResource(R.drawable.tesla_cool);
                } else {
                    set_warm = "0";
                    set_cool = "0";
                    cool_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.cool_off));
                    wind.setImageResource(R.drawable.tesla_off);
                }
                SetTask setTask = new SetTask();
                setTask.execute();
            }
        });

        // main thread
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //DB설정 온도 받기 시작
                                DownTask downTask = new DownTask();
                                downTask.execute();
                                //DB설정 온도 받기 종료


                            }

                        });

                        Thread.sleep(10000); // 1 분마다
////DB설정 온도 삽입
//                        SetTask setTask = new SetTask();
//                        setTask.execute();
//                        //DB설정 온도 삽입

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();


        // Inflate the layout for this fragment
        return ll;
    }

    class DownTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {

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
                    //DB에서 받은 값을 변수에 저장함
                    set_Temp = outResult[0];
                    set_cool = outResult[1];
                    set_warm = outResult[2];
                    indoor_temp = outResult[5];
                    set_charging_amount = outResult[6];
                    charging_port = outResult[7];
                    code = outResult[8];
                    set_wind = outResult[9];
                    Log.i("----", set_Temp);
//                    Log.i("----",set_cool);
//                    Log.i("----",set_warm);
//                    Log.i("----",set_charging_amount);
//                    Log.i("----",charging_port);
//                    Log.i("----",code);
//                    Log.i("----",set_wind);


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
            temp_set.setText(set_Temp);
            SetTask setTask = new SetTask();
            setTask.execute();
        }
    }  //updateTask종료

    //------------------------------------------------------------------------------
    class SetTask extends AsyncTask<String, Void, String> {

        String url;


        SetTask() {
        }

        SetTask(String url) {
            this.url = url;
        }


        @Override
        protected void onPreExecute() {
            //실행전 설정값이 바뀐것을 받는다.

            set_Temp = temp_set.getText().toString(); //사용자가 변경한 값을 받아서 저장
            if (set_warm.equals("1")) {
                heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_on));
                cool_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.cool_off));
                wind.setImageResource(R.drawable.tesla_heat);

                cool_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.cool_off));
            } else if (set_cool.equals("1")) {
                heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_off));

                cool_bt.setBackgroundDrawable(getContext().getDrawable((R.drawable.cool_on)));
                heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_off));
                wind.setImageResource(R.drawable.tesla_cool);
            } else {
                cool_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.cool_off));
                heat_bt.setBackgroundDrawable(getContext().getDrawable(R.drawable.heat_off));
                wind.setImageResource(R.drawable.tesla_off);
            }

        }

        @Override
        protected String doInBackground(String... strings) {


            //http request
            StringBuilder sb = new StringBuilder();
            URL url;
            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {

                url = new URL("http://70.12.114.147/ws/setTemp.do?&set_temp=" + set_Temp + "&set_wind=" + set_wind + "&set_cool=" + set_cool + "&set_warm=" + set_warm + "&set_charging_amount=" + set_charging_amount + "&charging_port=" + charging_port + "&code=" + code);
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


        }


    }

    @Override
    public void onDestroy() {
        flag=false;
        super.onDestroy();
    }
    //SetTask
}


