package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Frag1.Post> posts;

    public PostAdapter() {
        this.posts = new ArrayList<>();
    }

    public void setPosts(List<Frag1.Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Frag1.Post post = posts.get(position);
        holder.titleTextView.setText(post.getPostTitle());
        holder.contentTextView.setText(post.getPostContent());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;

        PostViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.post_title);
            contentTextView = view.findViewById(R.id.post_content);
        }
    }
}
