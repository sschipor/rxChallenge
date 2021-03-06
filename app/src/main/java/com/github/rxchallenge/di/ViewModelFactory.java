package com.github.rxchallenge.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.rxchallenge.db.AppDB;
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
    private AppDB appDB;

    public ViewModelFactory(ApiClient apiClient, AppDB appDB) {
        this.apiClient = apiClient;
        this.appDB = appDB;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (modelClass.isAssignableFrom(PostsViewModel.class)) {
                return modelClass.getConstructor(PostRepo.class).newInstance(
                        new PostRepo(apiClient, appDB)
                );
            } else if (modelClass.isAssignableFrom(CommentsViewModel.class)) {
                return modelClass.getConstructor(CommentRepo.class, PostRepo.class).newInstance(
                        new CommentRepo(apiClient, appDB),
                        new PostRepo(apiClient, appDB)
                );
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            //no matching constructor found -- thus no valid viewmodel
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
