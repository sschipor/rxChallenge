package com.github.rxchallenge.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rxchallenge.R;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.di.InjectionHelper;
import com.github.rxchallenge.di.ViewModelFactory;
import com.github.rxchallenge.network.utils.RepoResponse;

import java.util.List;

public class PostsFragment extends Fragment {

    private PostsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.posts_fragment, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(
                this,
                new ViewModelFactory(InjectionHelper.getInstance().provideApiClient())
        ).get(PostsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getPosts().observe(getViewLifecycleOwner(), new Observer<RepoResponse<List<Post>>>() {
            @Override
            public void onChanged(RepoResponse<List<Post>> listRepoResponse) {
                switch (listRepoResponse.getStatus()) {
                    case LOADING:

                        break;
                    case SUCCESS:
                        Navigation.findNavController(view).navigate(R.id.action_postsFragment_to_commentsFragment);
                        break;
                    case ERROR:

                        break;
                }
            }
        });
    }
}
