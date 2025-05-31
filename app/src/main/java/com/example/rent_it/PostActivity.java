package com.example.rent_it;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
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
        post.setOnClickListener(v -> uploadImage());
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
        progressDialog.show();

        if (imageUrl != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUrl));
            uploadTask = fileReference.putFile(imageUrl);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) throw task.getException();
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    myUrl = task.getResult().toString();
                    savePostToDatabase();
                    progressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(PostActivity.this, "Не удалось загрузить", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Выберите изображение", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
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

        // Поля из Spinner
        hashMap.put("type", spinnerType.getSelectedItem().toString());
        hashMap.put("buildingType", spinnerBuildingType.getSelectedItem().toString());
        hashMap.put("heating", spinnerHeating.getSelectedItem().toString());
        hashMap.put("wifi", spinnerWifi.getSelectedItem().toString());

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
            Glide.with(this).load(imageUrl).into(image_added);
        }
    }
}
