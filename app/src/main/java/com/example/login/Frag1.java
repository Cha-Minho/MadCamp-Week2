package com.example.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class Frag1 extends Fragment {
    private static final String URL = "https://your-api-url.com/posts/"; // replace with your server's URL
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private RecyclerView recyclerView;
    private FloatingActionButton fabNewPost;
    private PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag1, container, false);

        recyclerView = view.findViewById(R.id.post_recycler_view);
        fabNewPost = view.findViewById(R.id.fab_new_post);

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the adapter
        postAdapter = new PostAdapter();
        recyclerView.setAdapter(postAdapter);

        // Set up the FloatingActionButton
        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, you can start a new activity to create a new post
                Intent intent = new Intent(getActivity(), NewPostActivity.class);
                startActivity(intent);
            }
        });

        loadPosts();

        return view;
    }

    public class Post {
        private int postId;
        private String postTitle;
        private String postContent;
        private int userId;
        private String postDate;

        public Post(int postId, String postTitle, String postContent, int userId, String postDate) {
            this.postId = postId;
            this.postTitle = postTitle;
            this.postContent = postContent;
            this.userId = userId;
            this.postDate = postDate;
        }

        // Add getters and setters here
        // ...

        public String getPostTitle() {
            return postTitle;
        }

        public void setPostTitle(String postTitle) {
            this.postTitle = postTitle;
        }

        public String getPostContent() {
            return postContent;
        }

        public void setPostContent(String postContent) {
            this.postContent = postContent;
        }
    }

    private void loadPosts() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
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

                        List<Post> posts = new ArrayList<>();
                        for (int i = 0; i < postsJsonArray.length(); i++) {
                            JSONObject postJson = postsJsonArray.getJSONObject(i);

                            int postId = postJson.getInt("PostID");
                            String postTitle = postJson.getString("PostTitle");
                            String postContent = postJson.getString("PostContext");
                            int userId = postJson.getInt("UserID");
                            String postDate = postJson.getString("PostDate");

                            Post post = new Post(postId, postTitle, postContent, userId, postDate);
                            posts.add(post);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postAdapter.setPosts(posts); // update the adapter with the new posts
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

