package com.example.student.phone;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {

    EditText email1, pwd1, name1, publicdistance1, personalmileage1;
    ProgressDialog progressDialog;
    LoginTask loginTask;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        email1 = findViewById(R.id.email);
        pwd1 = findViewById(R.id.pwd);
        name1 = findViewById(R.id.name);
        publicdistance1 = findViewById(R.id.publicdistance);
        personalmileage1 = findViewById(R.id.personalmileage);
        progressDialog = new ProgressDialog(JoinActivity.this);

    }


    public void clickBt(View v) {

        String email = email1.getText().toString().trim();
        String pwd = pwd1.getText().toString().trim();
        //한글입력 오류 안나는 코드 시작
        String str = name1.getText().toString().trim();
        String name = null;
        try {
            name = java.net.URLEncoder.encode(new String(str.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //한글입력 오류 안나는 코드 종료

        String publicdistance = publicdistance1.getText().toString().trim();
        String personalmileage = personalmileage1.getText().toString().trim();

        if (email == null || pwd == null || name == null || publicdistance == null || personalmileage == null || email.equals("") || pwd.equals("") || name.equals("") || publicdistance.equals("") || personalmileage.equals("")) {
            return;
        }
        loginTask = new LoginTask("http://70.12.114.147/ws/join.do?email=" + email + "&pwd=" + pwd+"&publicDistance="+publicdistance+"&personalMileage="+personalmileage + "&name=" + name);
        loginTask.execute();

    }


    class LoginTask extends AsyncTask<String, Void, String> {

                    String url;

                    LoginTask() {
                    }

                    LoginTask(String url) {
                        this.url = url;
                    }


                    @Override
                    protected void onPreExecute() {
                        progressDialog.setMessage("SIGN UP");
                        progressDialog.show();
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
                            progressDialog.dismiss();
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

            progressDialog.dismiss();


            if (s.equals("1")) {
                Toast.makeText(JoinActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            } else {

                Toast.makeText(JoinActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                email1.setText("");
                pwd1.setText("");
                name1.setText("");
                publicdistance1.setText("");
                personalmileage1.setText("");

            }


        }

    } //LoginTask


}
