package com.github.rxchallenge.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rxchallenge.R;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.fragment.PostsFragmentDirections;

import java.util.Objects;

/**
 * @author Sebastian Schipor
 */
public class PostsAdapter extends ListAdapter<Post, PostsAdapter.PostVH> {

    private PostListCallback callback;

    public interface PostListCallback {
        void onFavoriteChanged(int postId, boolean isFavorite);
    }

    public PostsAdapter(@NonNull DiffUtil.ItemCallback<Post> diffCallback, PostListCallback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public PostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostVH(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PostVH holder, int position) {
        holder.bindItem(getItem(position));
    }

    class PostVH extends RecyclerView.ViewHolder {

        TextView title;
        TextView body;
        Button btnFav;

        public PostVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            btnFav = itemView.findViewById(R.id.btnFav);
        }

        private void bindItem(final Post post) {
            title.setText(post.title);
            body.setText(post.body);
            if (post.isFavorite) {
                btnFav.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
                btnFav.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorWindow));
            } else {
                btnFav.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorWindow));
                btnFav.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorBlack));
            }

            itemView.setOnClickListener(view -> {
                //push comments view
                PostsFragmentDirections.ActionPostsFragmentToCommentsFragment action =
                        PostsFragmentDirections.actionPostsFragmentToCommentsFragment(post.id);
                Navigation.findNavController(itemView).navigate(action);
            });

            btnFav.setOnClickListener(view -> {
                //notify fragment to call update post in DB
                //the list item will be refreshed automatically
                if (callback != null) {
                    callback.onFavoriteChanged(post.id, !post.isFavorite);
                }
            });
        }
    }

    public static class DiffItemCallback extends DiffUtil.ItemCallback<Post> {

        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.title.equals(newItem.title) && Objects.equals(oldItem.body, newItem.body)
                    && oldItem.isFavorite == newItem.isFavorite;
        }
    }
}
