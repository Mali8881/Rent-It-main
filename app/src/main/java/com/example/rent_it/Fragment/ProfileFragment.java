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
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rent_it.BookingsActivity;
import com.example.rent_it.FavoritesActivity;
import com.example.rent_it.LoginActivity;
import com.example.rent_it.Model.User;
import com.example.rent_it.MyBookingsActivity;
import com.example.rent_it.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
public class ProfileFragment extends Fragment {
    // Объяви все нужные View
    private ImageView profileImage;
    private TextView username, email, bio, rating, badge;
    private LinearLayout bookingTile, favoriteTile, myPostsTile, settingsTile;
    private ImageButton btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Найди View по id
        profileImage = v.findViewById(R.id.profile_image);
        username = v.findViewById(R.id.username);
        email = v.findViewById(R.id.email);
        bio = v.findViewById(R.id.bio);
        rating = v.findViewById(R.id.rating);
        badge = v.findViewById(R.id.badge);
        btnLogout = v.findViewById(R.id.btn_logout);

        // Для плиток — если у тебя GridLayout с LinearLayout внутри, назначь id этим LinearLayout и найди их
        bookingTile = v.findViewById(R.id.tile_booking);      // Присвой эти id в xml плиткам!
        favoriteTile = v.findViewById(R.id.tile_favorite);
        myPostsTile = v.findViewById(R.id.tile_myposts);
        settingsTile = v.findViewById(R.id.tile_settings);

        // Установи тестовые данные (или получи из Firebase)
        username.setText("Балнур Байзакова");
        email.setText("balnur@email.com");
        bio.setText("В поиске идеального жилья");
        rating.setText("4.9");
        badge.setText("• Superhost");

        // Картинку профиля (если у тебя url из Firebase) — используй Glide/Picasso
        // Glide.with(this).load(imageUrl).into(profileImage);

        // Логика кликов:
        bookingTile.setOnClickListener(v1 -> {
            // Открыть историю бронирований
            startActivity(new Intent(getContext(), MyBookingsActivity.class));
        });

        favoriteTile.setOnClickListener(v1 -> {
            // Открыть избранное
            startActivity(new Intent(getContext(), FavoritesActivity.class));
        });

        myPostsTile.setOnClickListener(v1 -> {
            // Открыть мои объявления
            startActivity(new Intent(getContext(), BookingsActivity.class));
        });

        settingsTile.setOnClickListener(v1 -> {
            // Открыть настройки
            startActivity(new Intent(getContext(), SettingsActivity.class));
        });

        btnLogout.setOnClickListener(v1 -> {
            // Выйти из аккаунта (например, FirebaseAuth)
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        return v;
    }
}
