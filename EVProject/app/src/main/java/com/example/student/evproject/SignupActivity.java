package com.example.student.evproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    EditText sName, sEmail, sPwd, sPwd_confirm, publicdistance,personalmileage;
    ProgressDialog progressDialog;
//    LoginTask loginTask;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sName = findViewById(R.id.signup_name);
        sEmail = findViewById(R.id.signup_email);
        sPwd = findViewById(R.id.signup_pwd);
        sPwd_confirm = findViewById(R.id.signup_ConfirmPwd);

        //비밀번호 일치 검사
       sPwd_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pwd = sPwd.getText().toString();
                String confirm = sPwd_confirm.getText().toString();

                if(pwd.equals(confirm)){
                    sPwd.setBackgroundColor(android.R.color.holo_green_dark);
                    sPwd_confirm.setBackgroundColor(android.R.color.holo_green_dark);
                }else{
                    sPwd.setBackgroundColor(android.R.color.holo_red_dark);
                    sPwd_confirm.setBackgroundColor(android.R.color.holo_red_dark);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void clickSubmit(View v){
        //입력확인
        if(sName.getText().toString().length() == 0){
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            sName.requestFocus();
        }
        if(sEmail.getText().toString().length() == 0){
            Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            sEmail.requestFocus();
        }

        if(sPwd.getText().toString().length() == 0){
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            sPwd.requestFocus();
        }
        if(sPwd_confirm.getText().toString().length() == 0){
            Toast.makeText(this, "비밀번호를 한 번 더 입력하세요.", Toast.LENGTH_SHORT).show();
            sPwd_confirm.requestFocus();
        }

        //비밀번호 일치 확인
        if(!sPwd.getText().toString().equals(sPwd_confirm.getText().toString())){
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            sPwd.setText("");
            sPwd_confirm.setText("");
            sPwd.requestFocus();
        }
        //Login 화면으로 이동 + email 데이터 전송
        Intent intent = new Intent();
        intent.putExtra("email",sEmail.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
