package com.github.rxchallenge.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.repo.CommentRepo;
import com.github.rxchallenge.repo.PostRepo;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CommentsViewModel extends ViewModel {

    private CommentRepo commentRepo;
    private PostRepo postRepo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CommentsViewModel(CommentRepo commentRepo, PostRepo postRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
    }

    LiveData<RepoResponse<Post>> getPost(int postId) {
        return postRepo.getPostById(postId, compositeDisposable);
    }

    LiveData<RepoResponse<List<Comment>>> getComments(int postId) {
       return commentRepo.getComments(postId, compositeDisposable);
    }

    void updateFavoritePost(int postId, boolean isFavorite) {
        compositeDisposable.add(postRepo.updateFavorite(postId, isFavorite)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
