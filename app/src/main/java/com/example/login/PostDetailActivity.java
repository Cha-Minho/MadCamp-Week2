package com.example.login;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView contentTextView;
    private Button reviseButton;
    private Button removeButton;
    private Button saveButton;

    private String postTitle;
    private String postContent;
    private int postId;
    private static final int EDIT_POST_REQUEST_CODE = 2;

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<PostComment> commentList; // Comment model 클래스를 참조합니다.

    private EditText commentInput;
    private Button postCommentButton;

    SharedPreferences sharedPref;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private EditText editTextComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Retrieve the data from the intent
        Intent intent = getIntent();
        postTitle = intent.getStringExtra("postTitle");
        postContent = intent.getStringExtra("postContent");
        postId = intent.getIntExtra("postId", -1);

        // Initialize the views
        titleTextView = findViewById(R.id.tv_title_detail);
        contentTextView = findViewById(R.id.tv_content_detail);
        reviseButton = findViewById(R.id.btn_revise_detail);
        removeButton = findViewById(R.id.btn_remove_detail);
        saveButton = findViewById(R.id.btn_save_detail);

        // Set the initial title and content
        titleTextView.setText(postTitle);
        contentTextView.setText(postContent);



        // Initialize the RecyclerView and CommentAdapter
        commentRecyclerView = findViewById(R.id.recycler_view_comments);
        commentList = new ArrayList<>(); // You should fetch actual comments from server

        // Load the comments for this post
        loadComments();

        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentAdapter);



        // Initialize the comment input and post comment button
        commentInput = findViewById(R.id.edit_text_comment);
        postCommentButton = findViewById(R.id.button_submit_comment);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
//                Toast.makeText(PostDetailActivity됨.this, "postComment 안됨", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the click listener for the revise button
        reviseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the EditPostActivity with the current title and content
                Intent editIntent = new Intent(PostDetailActivity.this, EditPostActivity.class);
                editIntent.putExtra("postTitle", postTitle);
                editIntent.putExtra("postContent", postContent);
                editIntent.putExtra("postId", postId);
                startActivityForResult(editIntent, EDIT_POST_REQUEST_CODE);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRequest();
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void postComment() {
        String commentText = commentInput.getText().toString();
        TextView tv_comment_id = findViewById(R.id.comment_id);

        if (!commentText.isEmpty()) {
            // Create a Comment object with the input text and other necessary information,
            // then send it to the server. You should also add it to the commentList and notify
            // the commentAdapter that the data has changed.
            PostComment newComment = new PostComment(commentText, postId, R.id.comment_id);
            commentList.add(newComment);
            commentAdapter.notifyDataSetChanged();

            // TODO: Send the newComment to your server
            // context, post_id, user_id

//            editTextComment = findViewById(R.id.edit_text_comment);

            int userId = sharedPref.getInt("userId", -1);
            int post_Id = postId;
            Log.d("userID", String.valueOf(userId));
            if (userId != -1) {
                JSONObject json = new JSONObject();
                try {
                    json.put("context", commentInput.getText().toString());
                    json.put("post_id", post_Id);
                    json.put("user_id", userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url("https://92f1-192-249-19-234.ngrok-free.app/comment/")
                        .post(body)
                        .build();
                commentInput.setText(""); // Clear the input box

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d("TAG", "response: " + response.body().string());
                            String responseBody = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_comment_id.setText(responseBody);
                                }
                            });
//                            finish();
                        } else {
                            throw new IOException("Unexpected code " + response);
                        }
                    }
                });
            } else {
                Toast.makeText(PostDetailActivity.this, "Post 안됨", Toast.LENGTH_SHORT).show();
            }



        } else {
            Toast.makeText(PostDetailActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadComments() {
        OkHttpClient client = new OkHttpClient();

        // Build the request
        Request request = new Request.Builder()
                .url("https://92f1-192-249-19-234.ngrok-free.app/load_comments/" + postId + "/") // Assuming this is your URL
                .get()
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
                    // Parse the response. Assuming it's a JSON array of comments
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String commentText = jsonObject.getString("context"); // replace with your actual key
                            postId = jsonObject.getInt("post_id");
                            // Add other necessary data
                            PostComment comment = new PostComment(commentText, postId, R.id.comment_id);
                            commentList.add(comment);
                        }

                        // Important: You need to notify the adapter of the changes on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void removeRequest() {
        OkHttpClient client = new OkHttpClient();

        // Construct the JSON object
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("postId", postId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request body with the appropriate MediaType
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        // Build the request
        Request request = new Request.Builder()
                .url("https://92f1-192-249-19-234.ngrok-free.app/delete_post/") // replace with your actual URL and append the ID of the post
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_POST_REQUEST_CODE && resultCode == RESULT_OK) {
            // Retrieve the updated title and content from the EditPostActivity
            if (data != null) {
                String updatedTitle = data.getStringExtra("updatedTitle");
                String updatedContent = data.getStringExtra("updatedContent");

                // Update the views with the updated title and content
                titleTextView.setText(updatedTitle);
                contentTextView.setText(updatedContent);

                // Update the postTitle and postContent variables
                postTitle = updatedTitle;
                postContent = updatedContent;
            }
        }
    }
}

