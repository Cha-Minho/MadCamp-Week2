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
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(Frag1.Post post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

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
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;

        PostViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.post_title);
            contentTextView = view.findViewById(R.id.post_content);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Frag1.Post clickedPost = posts.get(position);
                        if (clickListener != null) {
                            clickListener.onItemClick(clickedPost);
                        }
                    }
                }
            });
        }

        void bind(Frag1.Post post) {
            titleTextView.setText(post.getPostTitle());
            contentTextView.setText(post.getPostContent());
        }
    }
}
