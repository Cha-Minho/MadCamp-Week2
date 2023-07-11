package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class EditPostActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;

    private String postTitle;
    private String postContent;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        titleEditText = findViewById(R.id.edit_text_title_revise);
        contentEditText = findViewById(R.id.edit_text_content_revise);
        saveButton = findViewById(R.id.button_save_revise);

        // Retrieve the initial title and content from the intent
        Intent intent = getIntent();
        postTitle = intent.getStringExtra("postTitle");
        postContent = intent.getStringExtra("postContent");
        postId = intent.getIntExtra("postId", -1);

        // Set the initial title and content in the EditText fields
        titleEditText.setText(postTitle);
        contentEditText.setText(postContent);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated title and content from the EditText fields
                String updatedTitle = titleEditText.getText().toString();
                String updatedContent = contentEditText.getText().toString();

                // Call the method to send the POST request
                sendPostToServer(updatedTitle, updatedContent);

                // Send the updated title and content back to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedTitle", updatedTitle);
                resultIntent.putExtra("updatedContent", updatedContent);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
    private void sendPostToServer(String title, String content) {
        OkHttpClient client = new OkHttpClient();

        // Construct the JSON object
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("postId", postId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request body with the appropriate MediaType
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        // Build the request
        Request request = new Request.Builder()
                .url("https://3f01-192-249-19-234.ngrok-free.app/edit_post/") // replace with your actual URL and append the ID of the post
                .post(body)
                .build();

        // Execute the request and retrieve the response
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Do something with the response
                }
            }
        });
    }

}
