package com.example.myapplication.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.Comment.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.author.setText(comment.getUserId());
        Log.e("aaaaaaaaaaaaaaa", comment.getCommentContext()+"onBindViewHolder:" );
        holder.text.setText(comment.getCommentContext());
        holder.date.setText(comment.getCommentTime());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView author, text, date;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuthor);
            text = itemView.findViewById(R.id.commentText);
            date = itemView.findViewById(R.id.commentDate);
        }
    }
}
