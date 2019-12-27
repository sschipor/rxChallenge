package com.github.rxchallenge.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.rxchallenge.db.entity.Post;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author Sebastian Schipor
 */
@Dao
public interface PostDao {

    @Query("SELECT * FROM posts WHERE userId = :userId")
    Flowable<List<Post>> getPosts(int userId);

    @Query("SELECT * FROM posts WHERE id = :postId")
    Flowable<Post> getPostById(int postId);

    @Query("SELECT * FROM posts WHERE userId = :userId AND isFavorite = 1")
    Flowable<List<Post>> getFavoritePosts(int userId);

    @Query("UPDATE posts SET isFavorite = :isFavorite WHERE id = :id")
    Completable updateFavorite(int id, boolean isFavorite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Post> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Post post);
}
