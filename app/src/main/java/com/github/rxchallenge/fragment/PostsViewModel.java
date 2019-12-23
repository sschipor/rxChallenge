package com.github.rxchallenge.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.repo.PostRepo;
import com.github.rxchallenge.utils.UserSession;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class PostsViewModel extends ViewModel {

    private PostRepo repo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int userId = UserSession.getInstance().getUserId();
    private LiveData<RepoResponse<List<Post>>> postList;

    public PostsViewModel(PostRepo repo) {
        this.repo = repo;
    }

    LiveData<RepoResponse<List<Post>>> getPosts() {
        if (postList == null) {
            postList = repo.getPosts(userId, compositeDisposable);
        }
        return postList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
