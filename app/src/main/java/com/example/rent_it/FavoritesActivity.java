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


import android.os.Bundle;
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
    private List<Post> favoritePosts = new ArrayList<>();
    private List<String> likedPostIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recycler_view_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, favoritePosts);
        recyclerView.setAdapter(postAdapter);

        loadLikedPosts();
    }

    private void loadLikedPosts() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("LikesByUser").child(uid);

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likedPostIds.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    likedPostIds.add(snap.getKey());
                }
                loadPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadPosts() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritePosts.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post != null && likedPostIds.contains(post.getPostId())) {
                        favoritePosts.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
