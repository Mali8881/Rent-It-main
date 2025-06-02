package com.example.rent_it;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.PostAdapter;
import com.example.rent_it.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> favoriteIds;
    private String currentUserId;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recycler_favorites);
        emptyText = findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        favoriteIds = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        loadFavorites();
    }

    private void loadFavorites() {
        DatabaseReference favRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserId)
                .child("favorites");

        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteIds.clear();
                postList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String postId = snap.getValue(String.class);
                    if (postId != null) {
                        favoriteIds.add(postId);
                    }
                }

                fetchPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchPosts() {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");

        for (String postId : favoriteIds) {
            postRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                        postAdapter.notifyDataSetChanged();
                        emptyText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        if (favoriteIds.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        }
    }
}
