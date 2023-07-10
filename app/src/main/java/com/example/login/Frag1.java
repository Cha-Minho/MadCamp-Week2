package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class Frag1 extends Fragment {
    public static final int NEW_POST_REQUEST_CODE = 1;
    private static final String URL = "https://92f1-192-249-19-234.ngrok-free.app/load_post/"; // replace with your server's URL
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private RecyclerView recyclerView;
    private FloatingActionButton fabNewPost;
    private PostAdapter postAdapter;

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

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

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Frag1.Post post) {
                // 클릭된 게시물의 상세 내용을 보여주는 액티비티로 전환하기 위한 Intent를 생성합니다.
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("postTitle", post.getPostTitle());
                intent.putExtra("postContent", post.getPostContent());
                intent.putExtra("postId", post.getPostId());
                startActivity(intent);
            }
        });


        // Set up the FloatingActionButton
        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, you can start a new activity to create a new post
                Intent intent = new Intent(getActivity(), NewPostActivity.class);
                startActivityForResult(intent, NEW_POST_REQUEST_CODE);
            }
        });

        loadPosts();

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadPosts();
        }
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

        public int getPostId() {
            return postId;
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

                            int postId = postJson.getInt("id");
                            String postTitle = postJson.getString("title");
                            String postContent = postJson.getString("context");
                            int userId = postJson.getInt("user_id");
                            String postDate = postJson.getString("date");

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

