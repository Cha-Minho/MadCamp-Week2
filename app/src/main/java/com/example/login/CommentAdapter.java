package com.example.login;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<PostComment> comments;

    public CommentAdapter(List<PostComment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        PostComment comment = comments.get(position);
        holder.commentTextView.setText(comment.getText());
        holder.comment_date_text_view.setText(comment.getDate());
        updateUserName(holder.author_name_text_view, comment.getUserId());
        // Set other fields as needed
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        Button deleteButton;

        TextView comment_date_text_view;
        TextView author_name_text_view;
        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.comment_text_view);
            comment_date_text_view = itemView.findViewById(R.id.comment_date_text_view);
            author_name_text_view = itemView.findViewById(R.id.author_name_text_view);
            deleteButton = itemView.findViewById(R.id.comment_delete);

            // Set click listener for delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the comment
                    int position = getAdapterPosition();

                    // If the comment position is valid, delete the comment
                    if (position != RecyclerView.NO_POSITION) {
                        PostComment commentToDelete = comments.get(position);
                        Log.d("deleting comment id", String.valueOf(commentToDelete.getCommentId()));
                        // TODO: Delete the comment from your server
                        deleteComment(commentToDelete.getCommentId());

                        // Delete the comment from the list
                        comments.remove(position);

                        // Notify the adapter
                        notifyItemRemoved(position);
                    }
                }
            });

            // Initialize other views as needed
        }
    }

    private void deleteComment(int commentId) {
        OkHttpClient client = new OkHttpClient();

        // JSON object to hold the information, which is sent to the server
        JSONObject postData = new JSONObject();
        try {
            postData.put("commentId", commentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a body with text/plain MediaType and JSON format
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, postData.toString());

        // Create a request with the provided URL, and the post method
        Request request = new Request.Builder()
                .url("https://3f01-192-249-19-234.ngrok-free.app/delete_comments/") // replace with your server url
                .post(body)
                .build();

        // Enqueue the asynchronous request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("Response", response.body().string());
                }
            }
        });
    }

    private void updateUserName(TextView authorNameView, int userId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String jsonString = "{\"userId\": " + userId + "}";
        RequestBody body = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .url("https://3f01-192-249-19-234.ngrok-free.app/ur_name/") // replace with your server URL
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if(response.isSuccessful()) {
                        String jsonData = response.body().string();
                        JSONObject Jobject = new JSONObject(jsonData);
                        String userName = Jobject.getString("name");

                        // Update the TextView on the UI thread
                        new Handler(Looper.getMainLooper()).post(() -> authorNameView.setText(userName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
