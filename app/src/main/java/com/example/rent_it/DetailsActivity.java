package com.example.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView title, description, price, location, author,
            bedrooms, bathrooms, area, type, amenities, rating;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.details_image);
        title = findViewById(R.id.details_title);
        description = findViewById(R.id.details_description);
        price = findViewById(R.id.details_price);
        location = findViewById(R.id.details_location);
        author = findViewById(R.id.details_author);
        bedrooms = findViewById(R.id.details_bedrooms);
        bathrooms = findViewById(R.id.details_bathrooms);
        area = findViewById(R.id.details_area);
        type = findViewById(R.id.details_type);
        amenities = findViewById(R.id.details_amenities);
        rating = findViewById(R.id.details_rating);
        btnBook = findViewById(R.id.details_btn_book);

        Intent intent = getIntent();

        title.setText(intent.getStringExtra("title"));
        description.setText(intent.getStringExtra("description"));
        price.setText("Цена: " + intent.getStringExtra("price"));
        location.setText("Локация: " + intent.getStringExtra("location"));
        author.setText("Автор: " + intent.getStringExtra("author"));
        bedrooms.setText("Спальни: " + intent.getStringExtra("bedrooms"));
        bathrooms.setText("Ванные: " + intent.getStringExtra("bathrooms"));
        area.setText("Площадь: " + intent.getStringExtra("area") + " м²");
        type.setText("Тип: " + intent.getStringExtra("type"));
        amenities.setText("Удобства: " + intent.getStringExtra("amenities"));
        rating.setText("Рейтинг: " + intent.getStringExtra("rating"));

        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView);

        btnBook.setOnClickListener(v -> {
            String postId = intent.getStringExtra("postId");
            if (postId != null && !postId.isEmpty()) {
                Intent bookIntent = new Intent(DetailsActivity.this, BookingActivity.class);
                bookIntent.putExtra("postId", postId);
                startActivity(bookIntent);
            } else {
                Toast.makeText(DetailsActivity.this, "Ошибка: ID жилья не найден", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
