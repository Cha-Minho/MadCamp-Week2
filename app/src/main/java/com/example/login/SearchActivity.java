package com.example.login;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SearchActivity extends AppCompatActivity {

    private EditText searchText;
    private RecyclerView searchResultsRecyclerView;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_search);

        ImageButton backButton = findViewById(R.id.back_button);
        searchText = findViewById(R.id.search_text);
        Button searchButton = findViewById(R.id.search_button);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(searchText.getText().toString());
            }
        });

        postAdapter = new PostAdapter();  // Initialize the postAdapter variable
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(postAdapter); // Use postAdapter here

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Frag1.Post post) {
                // 클릭된 게시물의 상세 내용을 보여주는 액티비티로 전환하기 위한 Intent를 생성합니다.
                Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
                intent.putExtra("postTitle", post.getPostTitle());
                intent.putExtra("postContent", post.getPostContent());
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("postDate", post.getPostDate());
                intent.putExtra("userId", post.getUserId());
                startActivity(intent);
            }
        });

    }


    private void performSearch(String query) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("searchQuery", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("https://3f01-192-249-19-234.ngrok-free.app/search_post/")
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
                    String responseBody = response.body().string();

                    try {
                        JSONArray postsJsonArray = new JSONArray(responseBody);

                        List<Frag1.Post> posts = new ArrayList<>();
                        for (int i = 0; i < postsJsonArray.length(); i++) {
                            JSONObject postJson = postsJsonArray.getJSONObject(i);

                            int postId = postJson.getInt("id");
                            String postTitle = postJson.getString("title");
                            String postContent = postJson.getString("context");
                            int userId = postJson.getInt("user_id");
                            String postDate = postJson.getString("date");

                            Frag1.Post post = new Frag1.Post(postId, postTitle, postContent, userId, postDate);
                            posts.add(post);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update the RecyclerView with the new posts
                                postAdapter.setPosts(posts);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

}
