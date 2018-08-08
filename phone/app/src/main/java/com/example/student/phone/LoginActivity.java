package com.example.student.phone;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    public static String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Android/com.4th.pdh/longin";
    public static String filename1 = "what_the_fork";
    public static String filename2 = "reason_why_fork";
    public static String userID = "";
    public static String userPW = "";
    ImageView imageView;
    EditText editText1, editText2;
    TextView textView1, textView2;
    ProgressDialog progressDialog;
    Button button;
    private SharedPreferences sf;
    LoginTask loginTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageView=findViewById(R.id.imageView);
        AnimationDrawable animationDrawable = (AnimationDrawable)imageView.getDrawable();
        animationDrawable.start();

        File file = new File(dirPath);
        if (!file.exists())
            file.mkdirs();

        userID = readTextFile(filename1);
        userPW = readTextFile(filename2);

        if (!userID.equals("")) {
            Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
            startActivity(intent);
        }
        button = findViewById(R.id.button);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        progressDialog = new ProgressDialog(LoginActivity.this);

        sf = getSharedPreferences("loginData", MODE_PRIVATE);

        textView1 = findViewById(R.id.textView1);
        SpannableString content = new SpannableString("Sing up");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView1.setText(content);

        textView2 = findViewById(R.id.textView2);
        SpannableString content2 = new SpannableString("Reset password");
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        textView2.setText(content2);


    }

    public void join(View v) {
        Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
        startActivity(intent);

    }

    public void reset(View v) {
        Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
        startActivity(intent);

    }

    public void clickBt(View v) {

        userID = editText1.getText().toString().trim();
        userPW = editText2.getText().toString().trim();
        if (userID == null || userPW == null || userID.equals("") || userPW.equals("")) {
            return;
        }
        loginTask = new LoginTask("http://70.12.114.148/springTest/login.do?email=" + userID + "&pwd=" + userPW);
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

                editText1.setText("");
                editText2.setText("");

                writeTextFile(filename1, userID);
                writeTextFile(filename2, userPW);

                Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);

                intent.putExtra("name", s.substring(2));
                startActivity(intent);

            } else {

                editText1.setText("");
                editText2.setText("");
                Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void saveLoginData() {
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("email", userID);
        editor.commit();
    }

    public String readTextFile(String filename) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            InputStream is = new FileInputStream(dirPath + "/" + filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

        } catch (Exception e) {
            Log.i("readTextFile", e.toString());
        }

        return stringBuffer.toString();
    }

    public void writeTextFile(String filename, String contents) {
        try {
            FileWriter fw = new FileWriter(dirPath + "/" + filename);
            fw.write(contents);
            fw.close();
        } catch (Exception e) {
            Log.v("writeTextFile ERROR", e.toString());
        }
    }
}