package com.github.rxchallenge.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.repo.CommentRepo;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class CommentsViewModel extends ViewModel {

    private CommentRepo repo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LiveData<RepoResponse<List<Comment>>> commentList;

    public CommentsViewModel(CommentRepo repo) {
        this.repo = repo;
    }

    LiveData<RepoResponse<List<Comment>>> getComments(int postId) {
        if (commentList == null) {
            commentList = repo.getComments(postId, compositeDisposable);
        }
        return commentList;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
