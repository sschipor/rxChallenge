package com.github.rxchallenge.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rxchallenge.R;
import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.di.InjectionHelper;
import com.github.rxchallenge.di.ViewModelFactory;
import com.github.rxchallenge.network.utils.RepoResponse;

import java.util.List;

public class CommentsFragment extends Fragment {

    private CommentsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comments_fragment, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(
                this
                , new ViewModelFactory(InjectionHelper.getInstance().provideApiClient())
        ).get(CommentsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getComments(1).observe(getViewLifecycleOwner(), new Observer<RepoResponse<List<Comment>>>() {
            @Override
            public void onChanged(RepoResponse<List<Comment>> listRepoResponse) {
                switch (listRepoResponse.getStatus()) {
                    case LOADING:

                        break;
                    case SUCCESS:

                        break;
                    case ERROR:

                        break;
                }
            }
        });
    }
}
