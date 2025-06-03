package com.example.rent_it;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Uri imageUrl;
    private String myUrl = "";
    private StorageReference storageReference;
    private UploadTask uploadTask;

    // View элементы
    private EditText etDescription, etTitle, etPrice, etLocation,
            etBedrooms, etBathrooms, etArea, etAmenities;

    private Spinner spinnerType, spinnerBuildingType, spinnerHeating, spinnerWifi;

    private ImageView image_added, close;
    private TextView post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Инициализация EditText
        etDescription = findViewById(R.id.description);
        etTitle = findViewById(R.id.title);
        etPrice = findViewById(R.id.price);
        etLocation = findViewById(R.id.location);
        etBedrooms = findViewById(R.id.bedrooms);
        etBathrooms = findViewById(R.id.bathrooms);
        etArea = findViewById(R.id.area);
        etAmenities = findViewById(R.id.amenities);

        // Инициализация Spinner
        spinnerType = findViewById(R.id.spinner_type);
        spinnerBuildingType = findViewById(R.id.spinner_building_type);
        spinnerHeating = findViewById(R.id.spinner_heating);
        spinnerWifi = findViewById(R.id.spinner_wifi);

        // Настройка спиннеров
        setupSpinners();

        // Инициализация кнопок
        image_added = findViewById(R.id.image_added);
        close = findViewById(R.id.close);
        post = findViewById(R.id.post);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        image_added.setOnClickListener(v -> openImagePicker());
        close.setOnClickListener(v -> {
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        });
        post.setOnClickListener(v -> {
            if (imageUrl == null) {
                Toast.makeText(this, "Выберите изображение", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Квартира", "Дом", "Офис", "Отель", "Комната", "Гостинка", "Другое"}
        );
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Многоэтажка", "Частный дом", "Бизнес-центр", "Гостиница", "Другое"}
        );
        spinnerBuildingType.setAdapter(buildingAdapter);

        ArrayAdapter<String> heatingAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Центральное", "Газовое", "Электрическое", "Без отопления"}
        );
        spinnerHeating.setAdapter(heatingAdapter);

        ArrayAdapter<String> wifiAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Есть", "Нет"}
        );
        spinnerWifi.setAdapter(wifiAdapter);
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Публикация...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUrl));
        uploadTask = fileReference.putFile(imageUrl);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) throw task.getException();
            return fileReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful() && task.getResult() != null) {
                myUrl = task.getResult().toString();
                if (myUrl != null && !myUrl.isEmpty() && myUrl.startsWith("http")) {
                    savePostToDatabase();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(PostActivity.this, "Ошибка загрузки картинки!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PostActivity.this, "Не удалось загрузить", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePostToDatabase() {
        String postId = FirebaseDatabase.getInstance().getReference("Posts").push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("image", myUrl);
        hashMap.put("description", etDescription.getText().toString());
        hashMap.put("title", etTitle.getText().toString());
        hashMap.put("publisher", userId);
        hashMap.put("email", userEmail);
        hashMap.put("price", etPrice.getText().toString());
        hashMap.put("location", etLocation.getText().toString());

        // Безопасно получаем значения из спиннеров
        hashMap.put("type", spinnerType.getSelectedItem() != null ? spinnerType.getSelectedItem().toString() : "не указано");
        hashMap.put("buildingType", spinnerBuildingType.getSelectedItem() != null ? spinnerBuildingType.getSelectedItem().toString() : "не указано");
        hashMap.put("heating", spinnerHeating.getSelectedItem() != null ? spinnerHeating.getSelectedItem().toString() : "не указано");
        hashMap.put("wifi", spinnerWifi.getSelectedItem() != null ? spinnerWifi.getSelectedItem().toString() : "не указано");

        // Остальные поля
        hashMap.put("bedrooms", etBedrooms.getText().toString());
        hashMap.put("bathrooms", etBathrooms.getText().toString());
        hashMap.put("area", etArea.getText().toString());
        hashMap.put("amenities", etAmenities.getText().toString());
        hashMap.put("rating", "0");
        hashMap.put("reviewCount", "0");

        FirebaseDatabase.getInstance().getReference("Posts").child(postId).setValue(hashMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.ic_placeholder)
                    .into(image_added);
        }
    }
}
