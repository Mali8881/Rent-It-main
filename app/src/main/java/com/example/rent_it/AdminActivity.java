package com.example.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.UserAdapter;
import com.example.rent_it.Adapter.PostAdapter;
import com.example.rent_it.Model.Post;
import com.example.rent_it.Model.User;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private Button btnUsers, btnPosts;
    private RecyclerView recyclerView;
    private List<User> userList = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();
    private UserAdapter userAdapter;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnUsers = findViewById(R.id.btn_view_users);
        btnPosts = findViewById(R.id.btn_view_posts);
        recyclerView = findViewById(R.id.recycler_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnUsers.setOnClickListener(v -> loadUsers());
        btnPosts.setOnClickListener(v -> loadPosts());
    }

    private void loadUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        userList.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userAdapter = new UserAdapter(AdminActivity.this, userList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        postList.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                postAdapter = new PostAdapter(AdminActivity.this, postList);
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
