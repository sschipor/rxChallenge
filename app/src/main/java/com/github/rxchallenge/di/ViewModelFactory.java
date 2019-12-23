package com.github.rxchallenge.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.rxchallenge.fragment.CommentsViewModel;
import com.github.rxchallenge.fragment.PostsViewModel;
import com.github.rxchallenge.network.ApiClient;
import com.github.rxchallenge.repo.CommentRepo;
import com.github.rxchallenge.repo.PostRepo;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Sebastian Schipor
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private ApiClient apiClient;

    public ViewModelFactory(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (modelClass.isAssignableFrom(PostsViewModel.class)) {
                return modelClass.getConstructor(PostRepo.class).newInstance(new PostRepo(apiClient));
            } else if (modelClass.isAssignableFrom(CommentsViewModel.class)) {
                return modelClass.getConstructor(CommentRepo.class).newInstance(new CommentRepo(apiClient));
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            //no matching constructor found -- thus no valid viewmodel
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
