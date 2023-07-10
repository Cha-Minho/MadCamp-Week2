package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class NewPostActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSubmit;
    private Button cancelPost;

    private static final String URL = "https://6ef2-192-249-19-234.ngrok-free.app/post/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSubmit = findViewById(R.id.btn_post_submit);
        cancelPost = findViewById(R.id.cancel_post);

        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postTitle = editTextTitle.getText().toString();
                String postContent = editTextContent.getText().toString();

                if (postTitle.isEmpty() || postContent.isEmpty()) {
                    Toast.makeText(NewPostActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    int userId = sharedPref.getInt("userId", -1);
                    Log.d("userID", String.valueOf(userId));
                    if (userId != -1) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("postTitle", postTitle);
                            json.put("postContent", postContent);
                            json.put("userId", userId);
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
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("TAG", "response: " + response.body().string());
                                    finish();
                                } else {
                                    throw new IOException("Unexpected code " + response);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(NewPostActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
