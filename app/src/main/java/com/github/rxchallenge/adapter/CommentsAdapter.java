package com.github.rxchallenge.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rxchallenge.R;
import com.github.rxchallenge.db.entity.Comment;

import java.util.Objects;

/**
 * @author Sebastian Schipor
 */
public class CommentsAdapter extends ListAdapter<Comment, CommentsAdapter.CommentVH> {

    public CommentsAdapter(@NonNull DiffUtil.ItemCallback<Comment> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public CommentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentVH(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVH holder, int position) {
        holder.bindItem(getItem(position));
    }

    class CommentVH extends RecyclerView.ViewHolder {

        TextView username;
        TextView body;

        public CommentVH(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            body = itemView.findViewById(R.id.body);
        }

        private void bindItem(final Comment comment) {
            username.setText(comment.name);
            body.setText(comment.body);
        }
    }

    public static class DiffItemCallback extends DiffUtil.ItemCallback<Comment> {

        @Override
        public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
            return oldItem.name.equals(newItem.name) && Objects.equals(oldItem.body, newItem.body)
                    && oldItem.email.equals(newItem.email);
        }
    }
}
