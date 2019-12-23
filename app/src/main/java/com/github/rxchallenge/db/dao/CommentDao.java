package com.github.rxchallenge.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.rxchallenge.db.entity.Comment;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author Sebastian Schipor
 */
@Dao
public interface CommentDao {
    //get paged posts
    @Query("SELECT * FROM comments WHERE postId = :postId")
    Flowable<List<Comment>> getComments(int postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Comment> comments);
}
