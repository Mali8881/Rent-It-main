package com.example.rent_it;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.PostAdapter;
import com.example.rent_it.Adapter.UserAdapter;
import com.example.rent_it.Model.Post;
import com.example.rent_it.Model.User;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private Button btnUsers, btnPosts;
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private PostAdapter postAdapter;
    private final List<User> userList = new ArrayList<>();
    private final List<Post> postList = new ArrayList<>();

    private DatabaseReference userRef, postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnUsers = findViewById(R.id.btnUsers);
        btnPosts = findViewById(R.id.btnPosts);
        recyclerView = findViewById(R.id.recyclerViewAdmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        showUsers();

        btnUsers.setOnClickListener(v -> showUsers());
        btnPosts.setOnClickListener(v -> showPosts());
    }

    private void showUsers() {
        userList.clear();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null) userList.add(user);
                }

                userAdapter = new UserAdapter(AdminActivity.this, userList, new UserAdapter.OnUserActionListener() {
                    @Override public void onUserClicked(User user) {}

                    @Override public void onEditClicked(User user) {
                        Toast.makeText(AdminActivity.this, "Редактировать: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onDeleteClicked(User user) {
                        FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).removeValue();
                        Toast.makeText(AdminActivity.this, "Удалён: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                        showUsers();
                    }

                    @Override public void onSendMessageClicked(User user) {
                        Toast.makeText(AdminActivity.this, "Написать сообщение: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                    }
                });

                recyclerView.setAdapter(userAdapter);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showPosts() {
        postList.clear();
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post != null) postList.add(post);
                }
                postAdapter = new PostAdapter(AdminActivity.this, postList);
                recyclerView.setAdapter(postAdapter);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
