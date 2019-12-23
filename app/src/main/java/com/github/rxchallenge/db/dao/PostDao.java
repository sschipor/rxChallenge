package com.github.rxchallenge.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.rxchallenge.db.entity.Post;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * @author Sebastian Schipor
 */
@Dao
public interface PostDao {

    //get paged posts
    @Query("SELECT * FROM posts WHERE userId = :userId")
    Flowable<List<Post>> getPosts(int userId);

    @Query("SELECT * FROM posts WHERE id = :id")
    Single<Post> getPost(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Post> posts);
}
