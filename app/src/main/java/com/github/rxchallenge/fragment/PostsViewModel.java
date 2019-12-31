package com.github.rxchallenge.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.repo.PostRepo;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class PostsViewModel extends ViewModel {

    int userId;
    private PostRepo repo;
    private MutableLiveData<ViewType> viewType = new MutableLiveData<>(ViewType.ALL);
    //package private field used for testing
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public PostsViewModel(PostRepo repo) {
        this.repo = repo;
    }

    LiveData<RepoResponse<List<Post>>> postsResult =
            Transformations.switchMap(viewType, type ->
                    repo.getPosts(userId, type, compositeDisposable));

    LiveData<ViewType> getViewType() {
        return viewType;
    }

    void setUserId(int userId) {
        this.userId = userId;
    }

    void onViewTypeChanged(ViewType viewType) {
        this.viewType.setValue(viewType);
    }

    void updateFavoritePost(int postId, boolean isFavorite) {
        repo.updateFavorite(postId, isFavorite);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public enum ViewType {
        ALL, FAVORITE
    }
}
