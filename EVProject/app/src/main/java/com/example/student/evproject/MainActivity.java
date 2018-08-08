package com.example.student.evproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.location.LocationManager;
import android.location.LocationListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MapStyleOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView beam_low, beam_high, beam_fog, beam_backfog, warning_battery, warning_parkbreak, warning_belt, warning_repair;
    int bl, bh, bf, bb = 0;
    TextView speed, testbattery;
    TextView mainParking, mainReverse, mainNeutral, mainDrive;
    TextView start_km, start_mm, start_whkm, charging_km, charging_kwh, charging_whkm, month_km, month_kwh, month_whkm, total_mileage;
    ViewPager viewPager;
    Server server;
    boolean flag = true;
    ImageView battery;
    FloatingActionButton fab;

    TextView charging;

    Handler handler = new Handler(); // start_mm 갱신
    private int mm = 0;
    private String time = "";
    int beam_onoff = 0;

    double total_capacity = 90; //자동차의 배터리용량
    int lastCharge_capacity = 80; // 최근 충전종료시 배터리 용량
    int start_capacity = 80; //처음 시동시 배터리 용량
    double now_capacity = 80;  //현재 자동차의 배터리 용량
    double publicMileage = 3.9; //공인연비 3.9km/kWh;
    int startkm = 0;
    double startwhkm = 0;
    int chargekm = 50;
    double chargekwh = 12.2;
    double chargewhkm = 212;
    int monthkm = 2391;
    double monthkwh = 526;
    double monthwhkm = 220;

    int total_distance = 23433;
    double battary_percent= now_capacity/total_capacity*100;

    //tablet to oracle
    String code = "GENSDE123"; //자동차 코드 최초에 1번만 받음
    int CHARGING_STATUS = 0; // 1--> 충전중 0-->충전X
    float LATITUDE = (float) 37.501348;
    float LONGTITUDE = (float) 127.0391208;
    String MODEL_NAME = "MODELS_90D";
    String updateurl;
    String user_name = "4team"; //한글안됨

    private GoogleMap googleMap;
    private LocationManager locationManager;
    public static double mLatitude;
    public static double mLongitude;
    public static Pos srcPos;
    public static Pos dstPos;
    public static String[] data;

    EditText geoDst;
    TextView dmResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //상태바 제거
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

/*        //loading Activity불러오기
        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
        startActivity(intent);

        //login Activity 불러오기
        Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent2); */

        // 값 불러오기

        //chargingActivity로 전환
        charging = findViewById(R.id.gogo);
        charging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(MainActivity.this, ChargingActivity.class);
                startActivity(intent4);
            }
        });

        //상태표시등
        beam_low = findViewById(R.id.beam_low);
        beam_high = findViewById(R.id.beam_high);
        beam_fog = findViewById(R.id.beam_fog);
        beam_backfog = findViewById(R.id.beam_backfog);
        warning_battery = findViewById(R.id.warning_battery);
        warning_parkbreak = findViewById(R.id.warning_parkbreak);
        warning_belt = findViewById(R.id.warning_belt);
        warning_repair = findViewById(R.id.warning_repair);

        battery = findViewById(R.id.batteryview);

        //시동이후
        start_km = findViewById(R.id.start_km);
        start_mm = findViewById(R.id.start_mm);
        start_whkm = findViewById(R.id.start_whkm);

        //최근 충전 이후
        charging_km = findViewById(R.id.charging_km);
        charging_kwh = findViewById(R.id.charging_kwh);
        charging_whkm = findViewById(R.id.charging_whkm);

        //월간
        month_km = findViewById(R.id.month_km);
        month_kwh = findViewById(R.id.month_kwh);
        month_whkm = findViewById(R.id.month_whkm);

        //누적주행거리
        total_mileage = findViewById(R.id.total_mileage);

        //속도
        speed = findViewById(R.id.textView);

        //콘텐츠 페이지
        viewPager = findViewById(R.id.pager);

        //
        geoDst = findViewById(R.id.geoDst);
        dmResult = findViewById(R.id.dmResult);

        int v = 60; //속도
        speed.setText(""+v); //can값을 입력

        //배터리 퍼센테이지
        String percent= ""+battary_percent;
        testbattery = findViewById(R.id.testbattery);

        //기어변속기 PRND
        mainParking = findViewById(R.id.main_P);
        mainDrive = findViewById(R.id.main_D);

        mainDrive.setTextColor(Color.parseColor("#00fff0"));
        mainParking.setTextColor(Color.parseColor("#aaaaaa"));

        //fab
        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        //ControllActivity로 이동
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(MainActivity.this, ControllActivity.class);
                startActivity(intent3);
            }
        });


/*
       //시동 후 시간 카운트
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                time = String.valueOf(mm);
                                start_mm.setText(time);
                                //testbattery.setText(String.valueOf((int)battary_percent));

                                controlBeam();
                                //showBattery();

                                //start_km.setText(""+startkm);
                               // start_whkm.setText("" + startwhkm);

*/
/*                                String comma1=String.format("%,d",chargekm);
                                charging_km.setText(comma1);
                                charging_kwh.setText("" + chargekwh);
                                charging_whkm.setText("" + chargewhkm);

                                String comma2=String.format("%,d",monthkm);
                                month_km.setText(comma2);
                                month_kwh.setText("" + monthkwh);
                                month_whkm.setText("" + monthwhkm);
                                String comma3=String.format("%,d",total_distance);
                                total_mileage.setText(comma3);*//*


                            }
                        });
                        Thread.sleep(5000); // 1 분마다
                        mm += 1;

                        beam_onoff += 1;

                        */
/*startkm += 6;
                        chargekm += 6;
                        monthkm += 6;
                        total_distance += 6;*//*

                        //연비를 구해야함(시동 후 , 충전 후, 월간 후)
                        //시동 후 연비  (시동시 배터리용량-현재용량)*1000
                       //startwhkm = 256.4; //공인연비 적용값
                        //now_capacity = start_capacity - ((int) (chargewhkm * startkm) / 1000);
                        //now_capacity -= 5;
                        //chargekwh += ((int) (chargewhkm * startkm) / 1000);
                       // monthkwh += ((int) (chargewhkm * startkm) / 1000);
//                      chargewhkm=212.4; //임의값
//                      monthwhkm=257.4; //임의값
                        //battary_percent= now_capacity/total_capacity*100;
//현재 주행가능거리
                        //String AVAILABLE_DISTANCE = "" + (int) (now_capacity * 3.9);
                        //DB에 차량정보 업데이트
                        //updateurl = "http://70.12.114.147/ws/status_update.do?code=" + code + "&available_distance=" + AVAILABLE_DISTANCE + "&battery_capacity=" + now_capacity + "&indoor_temp=" + 20 + "&outdoor_temp=" + 15 + "&speed=" + 60+ "&charging_status=" + CHARGING_STATUS + "&charging_after_distance=" + chargekm + "&consumption_after_charging=" + chargekwh + "&monthly_distance=" + monthkm + "&monthly_battery_use=" + monthkwh + "&monthly_fuel_efficiency=" + monthwhkm + "&cumulative_mileage=" + total_distance + "&charge_amount=" + 80 + "&latitude=" + LATITUDE + "&longtitude=" + LONGTITUDE + "&model_name=" + MODEL_NAME  + "&user_name=" + user_name+ "&charging_after_fuel_efficiency=" + chargewhkm;

                        //UpdateTask updateTask = new UpdateTask(updateurl);
                        //updateTask.execute();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
*/


        //Fragment 슬라이드 위해 adatper 호출
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //Server Start
        try {
            server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mLatitude = 50.0379326;
        mLongitude = 8.5621518;
        srcPos = new Pos(mLatitude, mLongitude);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //GPS가 켜져있는지 체크
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            finish();
        }

        requestMyLocation();
    }

    //Control 상태표시등
    public void controlBeam(){
        if(beam_onoff == 1){
            beam_low.setVisibility(View.VISIBLE);
            beam_high.setVisibility(View.INVISIBLE);
            beam_fog.setVisibility(View.INVISIBLE);
            beam_backfog.setVisibility(View.INVISIBLE);

            warning_battery.setVisibility(View.INVISIBLE);
            warning_parkbreak.setVisibility(View.VISIBLE);
            warning_belt.setVisibility(View.INVISIBLE);
            warning_repair.setVisibility(View.INVISIBLE);
        }
        else if(beam_onoff == 2){
            beam_low.setVisibility(View.INVISIBLE);
            beam_high.setVisibility(View.VISIBLE);
            beam_fog.setVisibility(View.INVISIBLE);
            beam_backfog.setVisibility(View.INVISIBLE);

            warning_battery.setVisibility(View.INVISIBLE);
            warning_parkbreak.setVisibility(View.INVISIBLE);
            warning_belt.setVisibility(View.VISIBLE);
            warning_repair.setVisibility(View.INVISIBLE);
        }
        else if(beam_onoff == 3){
            beam_low.setVisibility(View.INVISIBLE);
            beam_high.setVisibility(View.INVISIBLE);
            beam_fog.setVisibility(View.VISIBLE);
            beam_backfog.setVisibility(View.INVISIBLE);

            warning_battery.setVisibility(View.INVISIBLE);
            warning_parkbreak.setVisibility(View.INVISIBLE);
            warning_belt.setVisibility(View.INVISIBLE);
            warning_repair.setVisibility(View.VISIBLE);
        }
        else if(beam_onoff == 4){
            beam_low.setVisibility(View.INVISIBLE);
            beam_high.setVisibility(View.INVISIBLE);
            beam_fog.setVisibility(View.INVISIBLE);
            beam_backfog.setVisibility(View.VISIBLE);

            warning_battery.setVisibility(View.VISIBLE);
            warning_parkbreak.setVisibility(View.INVISIBLE);
            warning_belt.setVisibility(View.INVISIBLE);
            warning_repair.setVisibility(View.INVISIBLE);

            beam_onoff = 0;
        }
    }

    //Battery 변경
    public void showBattery(){
        if(battary_percent < 100  && battary_percent >= 90){
            battery.setImageResource(R.drawable.b90);
        }else if(battary_percent < 90 && battary_percent >= 80){
            battery.setImageResource(R.drawable.b80);
        }else if(battary_percent < 80 && battary_percent >= 70){
            battery.setImageResource(R.drawable.b70);
        }else if(battary_percent < 70 && battary_percent >= 60){
            battery.setImageResource(R.drawable.b60);
        }else if(battary_percent < 60 && battary_percent >= 50){
            battery.setImageResource(R.drawable.b50);
        }else if(battary_percent < 50 && battary_percent >= 40){
            battery.setImageResource(R.drawable.b40);
        }else if(battary_percent < 40 && battary_percent >= 30){
            battery.setImageResource(R.drawable.b30);
        }else if(battary_percent < 30 && battary_percent >= 20){
            battery.setImageResource(R.drawable.b20);
        }else if(battary_percent < 20 && battary_percent >= 10){
            battery.setImageResource(R.drawable.b10);
        }else if(battary_percent <= 0){
            battery.setImageResource(R.drawable.b0);
        }
    }

    public void setSpeed(final String[] spl){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speed.setText(spl[0]);
                        //spl[1] --> latitude
                        //spl[2] --> longtitude
                        charging_km.setText(spl[3]);
                        charging_kwh.setText(spl[4]);
                        charging_whkm.setText(spl[5]);
                        month_km.setText(spl[6]);
                        month_kwh.setText(spl[7]);
                        month_whkm.setText(spl[8]);
                        start_km.setText(spl[9]);
                        total_mileage.setText(spl[10]);
                        battary_percent = Integer.parseInt(spl[11]);
                        showBattery();

                        srcPos.lat = Double.parseDouble(spl[1]);
                        srcPos.lng = Double.parseDouble(spl[2]);

                        mLatitude = srcPos.lat;
                        mLongitude = srcPos.lng;

                        Log.d("LATITUDE@@@@@", Double.toString(srcPos.lat));
                        Log.d("LONGITUDE@@@@@", Double.toString(srcPos.lng));

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MainActivity.this);

                    }
                });
            }
        };
        new Thread(r).start();
    }

    // HttpRequest Start ....
    class SendHttp extends AsyncTask<Void,Void,Void> {

        String surl="http://70.12.114.153/ws/main.do?speed=";
        URL url;
        HttpURLConnection urlConn;
        String speed;
        public SendHttp(){}
        public SendHttp(String speed){
            this.speed=speed;
            surl+=speed;
            try {
                url=new URL(surl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                urlConn= (HttpURLConnection) url.openConnection();
                urlConn.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    // HttpRequest End ....

    // ServerSocket Start .....

    public class Server extends Thread{

        ServerSocket serverSocket;
        boolean flag = true;
        boolean rflag = true;
        HashMap<String, DataOutputStream> map =
                new HashMap<>();

        public Server() throws IOException {
            // Create ServerSocket ...
            serverSocket = new ServerSocket(8268);
            Log.d("[Server]","Ready Server...");
        }

        @Override
        public void run() {
            // Accept Client Connection ...
            try {
                while(flag) {
                    Log.d("[Server]","Waiting Server...");
                    Socket socket =
                            serverSocket.accept();
                    String client = socket.getInetAddress().getHostAddress();
                    //setConnect(client, "t");
                    new Receiver(socket).start();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        class Receiver extends Thread{

            InputStream in;
            DataInputStream din;
            OutputStream out;
            DataOutputStream dout;
            Socket socket;
            String ip;

            public Receiver(Socket socket) {
                try {
                    this.socket = socket;
                    in = socket.getInputStream();
                    din = new DataInputStream(in);
                    out = socket.getOutputStream();
                    dout = new DataOutputStream(out);
                    ip = socket.getInetAddress().getHostAddress();
                    map.put(ip, dout);
                    System.out.println("Connected Count:"+map.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // end Recevier


            @Override
            public void run() {
                try {
                    while(rflag) {

                        if(socket.isConnected() &&
                                din != null && din.available() > 0 ) {

                            String str = din.readUTF();
                            String[] spl = str.split("/");

                            Log.d("[Server APP]",str);
                            setSpeed(spl);

                            SendHttp sendHttp = new SendHttp(str);
                            sendHttp.execute();
                        }
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    //setConnect(null,"f");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if(dout != null) {
                        try {
                            dout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(din != null) {
                        try {
                            din.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        public void sendAll(String msg) {
            System.out.println(msg);
            Sender sender = new Sender();
            sender.setMeg(msg);
            sender.start();
        }

        // Send Message All Clients
        class Sender extends Thread{

            String msg;

            public void setMeg(String msg) {
                this.msg = msg;
            }

            @Override
            public void run() {
                try {
                    Collection<DataOutputStream>
                            col = map.values();
                    Iterator<DataOutputStream>
                            it = col.iterator();
                    while(it.hasNext()) {
                        it.next().writeUTF(msg);
                    }

                }catch(Exception e) {
                    //e.printStackTrace();
                }
            }
        }

        public void stopServer() {
            rflag = false;
        }

    }

    // ServerSocket End .....

    class UpdateTask extends AsyncTask<String, Void, String> {

        String url;

        UpdateTask() {
        }

        UpdateTask(String url) {
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
                    Toast.makeText(MainActivity.this, "입력 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "입력 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    } //UpdateTask

    public void requestMyLocation(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
    }

    //위치정보 구하기 리스너
    LocationListener locationListener;

    {
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
/*                locationManager.removeUpdates(locationListener);

                srcPos.lat = location.getLatitude();
                srcPos.lng = location.getLongitude();

                mLatitude = srcPos.lat;
                mLongitude = srcPos.lng;

                Log.d("LATITUDE@@@@@", Double.toString(srcPos.lat));
                Log.d("LONGITUDE@@@@@", Double.toString(srcPos.lng));

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(MainActivity.this);*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("gps", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        LatLng position = new LatLng(mLatitude , mLongitude);
        this.googleMap.addMarker(new MarkerOptions().position(position).title("kikiki"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15    ));
    }

    public void runGeocoding(View view)  {

        final GeocodingAPI geocodingAPI = new GeocodingAPI();
        final String destination = geoDst.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dstPos = geocodingAPI.request(destination);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mLatitude = dstPos.lat;
                        mLongitude = dstPos.lng;

                        Log.d("LATITUDE@@@@@@", Double.toString(dstPos.lat));
                        Log.d("LONGITUDE@@@@@", Double.toString(dstPos.lng));

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MainActivity.this);

                        runDistanceMatrixAPI(srcPos, dstPos);
                    }
                });
            }
        }).start();
    }

    public void runDistanceMatrixAPI(Pos src, Pos dst){

        final DistanceMatrixAPI distanceMatrixAPI = new DistanceMatrixAPI();
        final Pos tsrc = src;
        //final Pos tdst = dst;
        final Pos tdst = new Pos(50.1172820, 8.6801750);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    data = distanceMatrixAPI.request(tsrc, tdst);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mLatitude = dstPos.lat;
                        mLongitude = dstPos.lng;

                        Log.d("DURATION@@@@@@", data[0]);
                        Log.d("DISTANCE@@@@@@", data[1]);

                        dmResult.setText(data[0]+" "+data[1]);
                    }
                });
            }
        }).start();
    }
} // end MainActivity