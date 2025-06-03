package com.example.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.rent_it.Fragment.GeminiChatFragment;
import com.example.rent_it.Fragment.HomeFragment;
import com.example.rent_it.Fragment.ProfileFragment;
import com.example.rent_it.Model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navigationItemSelectedListener);

        // Заполнение базы, если пусто
        fillFirebaseIfEmpty();

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new GeminiChatFragment();
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        }
    };

    // 👇 Добавь этот метод в MainActivity
    private void fillFirebaseIfEmpty() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    for (int i = 1; i <= 3; i++) {
                        String postId = postsRef.push().getKey();

                        Post post = new Post(
                                postId,
                                "https://picsum.photos/600/400?random=" + i,
                                "Описание поста " + i,
                                FirebaseAuth.getInstance().getCurrentUser() != null
                                        ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                                        : "test_user",
                                "Тестовое название " + i,
                                "example" + i + "@gmail.com",
                                "10000",
                                "Бишкек",
                                "2",
                                "1",
                                "55",
                                "Квартира",
                                "Многоэтажка",
                                "Центральное",
                                true,
                                "WiFi, Парковка",
                                "4.5",
                                System.currentTimeMillis()
                        );

                        postsRef.child(postId).setValue(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Ошибка Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
