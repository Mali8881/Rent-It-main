package com.example.rent_it.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rent_it.BookingsActivity;
import com.example.rent_it.FavoritesActivity;
import com.example.rent_it.LoginActivity;
import com.example.rent_it.MyBookingsActivity;
import com.example.rent_it.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView username, email, bio, rating, badge;
    private LinearLayout bookingTile, favoriteTile, myPostsTile, settingsTile;
    private ImageButton btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Найти View
        profileImage = v.findViewById(R.id.profile_image);
        username = v.findViewById(R.id.username);
        email = v.findViewById(R.id.email);
        bio = v.findViewById(R.id.bio);
        rating = v.findViewById(R.id.rating);
        badge = v.findViewById(R.id.badge);
        btnLogout = v.findViewById(R.id.btn_logout);

        bookingTile = v.findViewById(R.id.tile_booking);
        favoriteTile = v.findViewById(R.id.tile_favorite);
        myPostsTile = v.findViewById(R.id.tile_myposts);
        settingsTile = v.findViewById(R.id.tile_settings);

        // Загрузка данных из Firebase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(firebaseUser.getUid());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("username").getValue(String.class);
                        String emailStr = snapshot.child("email").getValue(String.class);
                        String bioStr = snapshot.child("bio").getValue(String.class);
                        String ratingStr = snapshot.child("rating").getValue(String.class);
                        String badgeStr = snapshot.child("badge").getValue(String.class);
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        username.setText(name != null ? name : "Без имени");
                        email.setText(emailStr != null ? emailStr : "user@email.com");
                        bio.setText(bioStr != null ? bioStr : "В поиске идеального жилья");
                        rating.setText(ratingStr != null ? ratingStr : "0.0");
                        badge.setText(badgeStr != null ? "• " + badgeStr : "");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(getContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_person)
                                    .into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Обработчики кликов
        bookingTile.setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), MyBookingsActivity.class));
        });

        favoriteTile.setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), FavoritesActivity.class));
        });

        myPostsTile.setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), BookingsActivity.class));
        });

        settingsTile.setOnClickListener(v1 -> {
            Toast.makeText(getContext(), "Настройки пока не реализованы", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return v;
    }
}
