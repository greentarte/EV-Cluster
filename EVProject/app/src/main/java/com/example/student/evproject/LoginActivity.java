package com.example.student.evproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    EditText login_email, login_pwd;
    CheckBox login_checkBox;
    String email = "";
    ProgressDialog progressDialog;
    LoginTask loginTask;
    private SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_email = findViewById(R.id.login_email);
        login_pwd = findViewById(R.id.login_pwd);
        login_checkBox = findViewById(R.id.login_checkBox);
        progressDialog = new ProgressDialog(LoginActivity.this);
        sf = getSharedPreferences("loginData",MODE_PRIVATE);
    }

    //SignUp Click
    public void clickSignUp(View v){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 1000);
    }

    // Login Click
    public void clickLogIn(View v){
            email = login_email.getText().toString().trim();
            String pwd = login_pwd.getText().toString().trim();
            if (email == null || pwd == null || email.equals("") || pwd.equals("")) {
                return;
            }
            loginTask = new LoginTask("http://70.12.114.148/springTest/login.do?email=" + email + "&pwd=" + pwd);
            loginTask.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("RESULT", requestCode + "");
        Log.d("RESULT", resultCode + "");
        Log.d("RESULT", data + "");

        //sign up 화면에서 데이터가 넘어오면 Toast 뜨게하기
        if(requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(LoginActivity.this, "회원가입을 완료했습니다!", Toast.LENGTH_SHORT).show();
            login_email.setText(data.getStringExtra("email"));
        }

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
            progressDialog.setMessage("Login");
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
                    con.setConnectTimeout(5000);   //connection 5초이상 길어지면 exepction
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


            if (s.charAt(0) == '1') {

                saveLoginData(); //아이디 값 sharedpreferences 저장함수
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                login_email.setText("");
                login_pwd.setText("");

                /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("name", s.substring(2));
                startActivity(intent);*/

                finish();

            } else {

                login_email.setText("");
                login_pwd.setText("");
                Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();

            }


        }

    }


    private void saveLoginData() {


        SharedPreferences.Editor editor = sf.edit();
        editor.putString("email", email);
        editor.commit();

    }
}
