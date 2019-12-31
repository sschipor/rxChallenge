package com.github.rxchallenge.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.repo.CommentRepo;
import com.github.rxchallenge.repo.PostRepo;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class CommentsViewModel extends ViewModel {

    private CommentRepo commentRepo;
    private PostRepo postRepo;
    //package private field used for testing
    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        postRepo.updateFavorite(postId, isFavorite);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
