package com.github.rxchallenge.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.repo.PostRepo;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PostsViewModel extends ViewModel {

    private PostRepo repo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int userId;
    private MutableLiveData<ViewType> viewType = new MutableLiveData<>(ViewType.ALL);

    public PostsViewModel(PostRepo repo) {
        this.repo = repo;
    }

    LiveData<RepoResponse<List<Post>>> getPostsResult =
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
        compositeDisposable.add(repo.updateFavorite(postId, isFavorite)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe());
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
