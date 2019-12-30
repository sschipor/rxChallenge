package com.github.rxchallenge;

import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;


import java.util.ArrayList;
import java.util.List;


public class TestUtils {

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
}