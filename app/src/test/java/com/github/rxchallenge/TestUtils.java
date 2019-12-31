package com.github.rxchallenge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;


import java.util.ArrayList;
import java.util.List;


public class TestUtils {
    public static String ERROR_RESPONSE = "ERROR";

    public static List<Post> mockPostList() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());

        return posts;
    }

    public static List<Comment> mockCommentsList() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        comments.add(new Comment());

        return comments;
    }

    public static LiveData<RepoResponse<List<Post>>> mockGetPostsResponseSuccess() {
        return new MutableLiveData<RepoResponse<List<Post>>>(RepoResponse.success(new ArrayList<Post>()));
    }

    public static LiveData<RepoResponse<List<Post>>> mockGetPostsResponseLoading() {
        return new MutableLiveData<RepoResponse<List<Post>>>(RepoResponse.loading());
    }

    public static LiveData<RepoResponse<List<Post>>> mockGetPostsResponseError() {
        return new MutableLiveData<RepoResponse<List<Post>>>(RepoResponse.error(ERROR_RESPONSE));
    }

    public static LiveData<RepoResponse<Post>> mockGetPostByIdLoading() {
        return new MutableLiveData<RepoResponse<Post>>(RepoResponse.loading());
    }

    public static LiveData<RepoResponse<Post>> mockGetPostByIdSuccess() {
        return new MutableLiveData<RepoResponse<Post>>(RepoResponse.success(new Post()));
    }

    public static LiveData<RepoResponse<Post>> mockGetPostByIdError() {
        return new MutableLiveData<RepoResponse<Post>>(RepoResponse.error(ERROR_RESPONSE));
    }

    public static LiveData<RepoResponse<List<Comment>>> mockGetCommentsLoading() {
        return new MutableLiveData<RepoResponse<List<Comment>>>(RepoResponse.loading());
    }

    public static LiveData<RepoResponse<List<Comment>>> mockGetCommentsSuccess() {
        return new MutableLiveData<RepoResponse<List<Comment>>>(RepoResponse.success(new ArrayList<Comment>()));
    }

    public static LiveData<RepoResponse<List<Comment>>> mockGetCommentsError() {
        return new MutableLiveData<RepoResponse<List<Comment>>>(RepoResponse.error(ERROR_RESPONSE));
    }
}