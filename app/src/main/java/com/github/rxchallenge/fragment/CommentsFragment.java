package com.github.rxchallenge.fragment;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rxchallenge.R;
import com.github.rxchallenge.activity.MainActivity;
import com.github.rxchallenge.adapter.CommentsAdapter;
import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.di.InjectionHelper;
import com.github.rxchallenge.di.ViewModelFactory;
import com.github.rxchallenge.network.utils.Status;

import java.util.List;


public class CommentsFragment extends Fragment {

    private CommentsViewModel mViewModel;
    private int postId;
    private CommentsAdapter adapter = new CommentsAdapter(new CommentsAdapter.DiffItemCallback());

    //views
    private TextView postTitle;
    private TextView postBody;
    private TextView noCommentsHint;
    private Button btnFav;
    private RecyclerView commentsRv;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comments_fragment, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(
                requireActivity(),
                new ViewModelFactory(InjectionHelper.getInstance().provideApiClient(),
                        InjectionHelper.getInstance().provideAppDatabase()
                )
        ).get(CommentsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            postId = CommentsFragmentArgs.fromBundle(getArguments()).getPostId();
        }

        //init views
        postTitle = view.findViewById(R.id.title);
        postBody = view.findViewById(R.id.body);
        noCommentsHint = view.findViewById(R.id.noCommentsHint);
        btnFav = view.findViewById(R.id.btnFav);
        commentsRv = view.findViewById(R.id.commentsRv);

        //setup the views
        setup();
        //retrieve post livedata to handle changes
        getPost();
        //retrieve comments for post
        getComments();
    }

    private void setup() {
        //call setup nav bar to sync toolbar with navigation graph
        ((MainActivity) getActivity()).setupNavBar();
        commentsRv.setAdapter(adapter);
    }

    private void getPost() {
        //retrieve post by id as LiveData to listen for any changes and update UI
        mViewModel.getPost(postId).observe(getViewLifecycleOwner(), postRepoResponse -> {
            if (postRepoResponse.getStatus() == Status.SUCCESS) {
                Post post = postRepoResponse.getData();
                if (post != null) {
                    postTitle.setText(post.title);
                    postBody.setText(post.body);
                    if (post.isFavorite) {
                        btnFav.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        btnFav.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWindow));
                    } else {
                        btnFav.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWindow));
                        btnFav.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    }

                    btnFav.setOnClickListener(view -> mViewModel.updateFavoritePost(post.id, !post.isFavorite));
                }
            }
        });
    }

    private void getComments() {
        mViewModel.getComments(postId).observe(getViewLifecycleOwner(), listRepoResponse -> {
            switch (listRepoResponse.getStatus()) {
                case LOADING:
                    ((MainActivity) getActivity()).showProgress(true);
                    break;
                case SUCCESS:
                    ((MainActivity) getActivity()).showProgress(false);
                    if (listRepoResponse.getData() != null) {
                        //update recycler
                        List<Comment> posts = listRepoResponse.getData();
                        if (posts == null || posts.size() == 0) {
                            commentsRv.setVisibility(View.GONE);
                            noCommentsHint.setVisibility(View.VISIBLE);
                        } else {
                            commentsRv.setVisibility(View.VISIBLE);
                            noCommentsHint.setVisibility(View.GONE);
                            adapter.submitList(posts);
                        }
                    }
                    break;
                case ERROR:
                    ((MainActivity) getActivity()).showProgress(false);
                    Toast.makeText(getContext(), listRepoResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
