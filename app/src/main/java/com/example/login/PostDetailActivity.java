package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

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
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentAdapter);

        // Load the comments for this post
        loadComments();

        // Initialize the comment input and post comment button
        commentInput = findViewById(R.id.edit_text_comment);
        postCommentButton = findViewById(R.id.button_submit_comment);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
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
        if (!commentText.isEmpty()) {
            // Create a Comment object with the input text and other necessary information,
            // then send it to the server. You should also add it to the commentList and notify
            // the commentAdapter that the data has changed.
            PostComment newComment = new PostComment(commentText);
            commentList.add(newComment);
            commentAdapter.notifyDataSetChanged();
            commentInput.setText(""); // Clear the input box
            // TODO: Send the newComment to your server

            context, post_id, user_id
        }
    }

    private void loadComments() {
        // Fetch the comments for this post from the server and add them to the commentList,
        // then notify the commentAdapter that the data has changed.

         commentAdapter.notifyDataSetChanged();
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

