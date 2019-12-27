package com.github.rxchallenge.network;

import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Sebastian Schipor
 */
public interface ApiClient {

    @GET("/posts")
    Single<List<Post>> getPosts(
            @Query("userId") int userId
    );

    @GET("/posts/{postId}")
    Single<Post> getPostById(
            @Path("postId") int postId
    );

    @GET("/comments")
    Single<List<Comment>> getComments(
            @Query("postId") int postId
    );

}
