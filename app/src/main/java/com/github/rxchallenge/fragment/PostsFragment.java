package com.github.rxchallenge.fragment;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rxchallenge.R;
import com.github.rxchallenge.activity.MainActivity;
import com.github.rxchallenge.adapter.PostsAdapter;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.di.InjectionHelper;
import com.github.rxchallenge.di.ViewModelFactory;

import java.util.List;

public class PostsFragment extends Fragment implements PostsAdapter.PostListCallback {

    private PostsViewModel mViewModel;
    private LoginViewModel loginViewModel;

    private RecyclerView postsRv;
    private TextView noPostsHint;

    private Button btnAll;
    private Button btnFav;

    private PostsAdapter adapter = new PostsAdapter(new PostsAdapter.DiffItemCallback(), this);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.posts_fragment, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(
                requireActivity(),
                new ViewModelFactory(InjectionHelper.getInstance().provideApiClient())
        ).get(PostsViewModel.class);
        loginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //bind views
        postsRv = view.findViewById(R.id.postsRv);
        noPostsHint = view.findViewById(R.id.noPostsHint);

        btnAll = view.findViewById(R.id.btnAll);
        btnFav = view.findViewById(R.id.btnAllFav);

        loginViewModel.authenticationState.observe(getViewLifecycleOwner(), authenticationState -> {
            switch (authenticationState) {
                case AUTHENTICATED:
                    setup();
                    getPosts();
                    break;
                case UNAUTHENTICATED:
                    Navigation.findNavController(view).navigate(R.id.loginFragment);
                    break;
            }
        });
    }

    private void setup() {
        ((MainActivity) getActivity()).setupNavBar();
        mViewModel.setUserId(loginViewModel.userId);

        postsRv.setAdapter(adapter);

        btnAll.setOnClickListener(view -> {
            mViewModel.onViewTypeChanged(PostsViewModel.ViewType.ALL);
        });
        btnFav.setOnClickListener(view -> {
            mViewModel.onViewTypeChanged(PostsViewModel.ViewType.FAVORITE);
        });

        //change bottom buttons background according to selected list view type
        mViewModel.getViewType().observe(getViewLifecycleOwner(), viewType -> {
            if (viewType == PostsViewModel.ViewType.ALL) {
                btnAll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                btnAll.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWindow));
                btnFav.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWindow));
                btnFav.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
            } else {
                btnFav.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                btnFav.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWindow));
                btnAll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWindow));
                btnAll.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
            }
        });
    }

    private void getPosts() {
        mViewModel.getPostsResult.observe(getViewLifecycleOwner(), listRepoResponse -> {
            switch (listRepoResponse.getStatus()) {
                case LOADING:
                    ((MainActivity) getActivity()).showProgress(true);
                    break;
                case SUCCESS:
                    ((MainActivity) getActivity()).showProgress(false);
                    if (listRepoResponse.getData() != null) {
                        //update recycler
                        List<Post> posts = listRepoResponse.getData();
                        if (posts == null || posts.size() == 0) {
                            postsRv.setVisibility(View.GONE);
                            noPostsHint.setVisibility(View.VISIBLE);
                        } else {
                            postsRv.setVisibility(View.VISIBLE);
                            noPostsHint.setVisibility(View.GONE);
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

    @Override
    public void onFavoriteChanged(int postId, boolean isFavorite) {
        mViewModel.updateFavoritePost(postId, isFavorite);
    }
}
