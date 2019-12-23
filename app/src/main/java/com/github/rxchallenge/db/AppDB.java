package com.github.rxchallenge.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.rxchallenge.db.dao.CommentDao;
import com.github.rxchallenge.db.dao.PostDao;
import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;

/**
 * @author Sebastian Schipor
 */
@Database(entities = {Post.class, Comment.class}, version = 1)
abstract public class AppDB extends RoomDatabase {

    public abstract PostDao getPostDao();

    public abstract CommentDao getCommentDao();
}
