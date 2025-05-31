package com.example.rent_it;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.CommentAdapter;
import com.example.rent_it.Model.Comment;
import com.example.rent_it.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private EditText commentEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private RecyclerView commentsRecyclerView;
    private List<Comment> commentList;
    private CommentAdapter adapter;
    private String postId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentEditText = findViewById(R.id.commentEditText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitCommentBtn);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        postId = getIntent().getStringExtra("postId");

        if (postId == null) {
            Toast.makeText(this, "Ошибка: нет ID поста", Toast.LENGTH_SHORT).show();
            finish(); // Закрыть экран, если нет postId
            return;
        }

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        commentsRecyclerView.setAdapter(adapter);

        submitButton.setOnClickListener(v -> submitComment());

        loadComments();
    }


    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();
        float rating = ratingBar.getRating();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (commentText.isEmpty() || rating == 0) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postId).push();
        Comment comment = new Comment(userId, commentText, rating, postId);
        ref.setValue(comment);
        commentEditText.setText("");
        ratingBar.setRating(0);
    }

    private void loadComments() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
