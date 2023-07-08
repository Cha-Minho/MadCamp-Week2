package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        //////// EditText login
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 로그인 처리 로직
                if (performLogin(username, password)) {
                    Toast.makeText(EmailActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    // 로그인 성공 시 다음 화면으로 이동
                     Intent intent = new Intent(EmailActivity.this, MainActivity.class);
                     startActivity(intent);
                } else {
                    Toast.makeText(EmailActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //////////
    }
    private boolean performLogin(String username, String password) {
        // 실제 로그인 처리를 구현해야 합니다.
        // 예를 들어, 서버로 요청을 보내고 응답을 받아 유효성을 검사하는 등의 작업을 수행합니다.
        // 이 예제에서는 간단히 "testuser"와 "password"라는 값으로 로그인이 성공하는 것으로 가정합니다.
        return username.equals("testuser") && password.equals("password");
    }
}