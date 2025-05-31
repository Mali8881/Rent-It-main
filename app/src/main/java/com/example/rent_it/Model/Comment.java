package com.example.rent_it.Model;

public class Comment {
    private String publisher;
    private String comment;
    private float rating;
    private String postId;

    public Comment() {} // Пустой конструктор для Firebase

    public Comment(String publisher, String comment, float rating, String postId) {
        this.publisher = publisher;
        this.comment = comment;
        this.rating = rating;
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }

    public String getPostId() {
        return postId;
    }
}
