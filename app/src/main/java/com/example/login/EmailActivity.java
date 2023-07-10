package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    Button loginButton;
    private static final String URL = "https://6ef2-192-249-19-234.ngrok-free.app/login/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        //////// EditText login
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                performLogin(email, password, new LoginCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(EmailActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EmailActivity.this, MainActivity.class);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(() -> Toast.makeText(EmailActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });

        //////////
    }
    interface LoginCallback {
        void onSuccess();
        void onFailure();
    }

    private void performLogin(String email, String password, LoginCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.onFailure());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String userIdStr = response.body().string();
                    try {
                        int userId = Integer.parseInt(userIdStr);

                        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("userId", userId);
                        editor.apply();

                        runOnUiThread(() -> callback.onSuccess());

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> callback.onFailure());
                    }
                } else {
                    runOnUiThread(() -> callback.onFailure());
                }
            }


        });
    }

}