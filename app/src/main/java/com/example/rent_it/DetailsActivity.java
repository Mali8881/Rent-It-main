package com.example.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvTitle, tvDescription, tvPrice, tvLocation, tvAuthor;
    private TextView tvBedrooms, tvBathrooms, tvArea, tvType, tvAmenities, tvRating;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.details_image);
        tvTitle = findViewById(R.id.details_title);
        tvDescription = findViewById(R.id.details_description);
        tvPrice = findViewById(R.id.details_price);
        tvLocation = findViewById(R.id.details_location);
        tvAuthor = findViewById(R.id.details_author);
        btnBook = findViewById(R.id.details_btn_book);

        tvBedrooms = findViewById(R.id.details_bedrooms);
        tvBathrooms = findViewById(R.id.details_bathrooms);
        tvArea = findViewById(R.id.details_area);
        tvType = findViewById(R.id.details_type);
        tvAmenities = findViewById(R.id.details_amenities);
        tvRating = findViewById(R.id.details_rating);

        // Получаем данные из интента
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String price = intent.getStringExtra("price");
        String location = intent.getStringExtra("location");
        String author = intent.getStringExtra("author");
        String postId = intent.getStringExtra("postId");
        String bedrooms = intent.getStringExtra("bedrooms");
        String bathrooms = intent.getStringExtra("bathrooms");
        String area = intent.getStringExtra("area");
        String type = intent.getStringExtra("type");
        String amenities = intent.getStringExtra("amenities");
        String rating = intent.getStringExtra("rating");

        // Установка значений
        Glide.with(this).load(image).into(imageView);
        tvTitle.setText(title != null ? title : "Нет заголовка");
        tvDescription.setText(description != null ? description : "Нет описания");
        tvPrice.setText("Цена: " + (price != null ? price : "-") + " сом");
        tvLocation.setText("Локация: " + (location != null ? location : "-"));
        tvAuthor.setText("Автор: " + (author != null ? author : "-"));

        tvBedrooms.setText("Спальни: " + (bedrooms != null ? bedrooms : "-"));
        tvBathrooms.setText("Ванные комнаты: " + (bathrooms != null ? bathrooms : "-"));
        tvArea.setText("Площадь: " + (area != null ? area : "-") + " м²");
        tvType.setText("Тип: " + (type != null ? type : "-"));
        tvAmenities.setText("Удобства: " + (amenities != null ? amenities : "-"));
        tvRating.setText("Рейтинг: " + (rating != null ? rating : "-"));

        // Переход на бронирование
        btnBook.setOnClickListener(v -> {
            Intent i = new Intent(DetailsActivity.this, BookingActivity.class);
            i.putExtra("postId", postId);
            startActivity(i);
        });
    }
}
