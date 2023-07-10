package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        // Set other fields as needed
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        Button deleteButton;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.comment_text_view);
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

                        // TODO: Delete the comment from your server


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

}
