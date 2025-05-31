package com.example.rent_it;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button registerBtn;
    private ImageView profileImage;
    private Uri imageUri;
    private static final int IMAGE_REQUEST = 1;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        profileImage = findViewById(R.id.profile_image);
        registerBtn = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Выбор фото
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);
        });

        registerBtn.setOnClickListener(v -> {
            String txtUsername = username.getText().toString();
            String txtEmail = email.getText().toString();
            String txtPassword = password.getText().toString();

            if (txtUsername.isEmpty() || txtEmail.isEmpty() || txtPassword.isEmpty()) {
                Toast.makeText(this, "Все поля обязательны", Toast.LENGTH_SHORT).show();
            } else if (txtPassword.length() < 6) {
                Toast.makeText(this, "Пароль должен быть не меньше 6 символов", Toast.LENGTH_SHORT).show();
            } else {
                register(txtUsername, txtEmail, txtPassword);
            }
        });
    }

    private void register(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                assert firebaseUser != null;
                String userId = firebaseUser.getUid();

                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                // Если фото выбрано
                if (imageUri != null) {
                    StorageReference fileRef = storageRef.child(userId + ".jpg");
                    fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                saveUserData(username, email, userId, imageUrl);
                            })
                    ).addOnFailureListener(e -> Toast.makeText(this, "Ошибка загрузки фото", Toast.LENGTH_SHORT).show());
                } else {
                    // Без фото — ставим default
                    saveUserData(username, email, userId, "default");
                }

            } else {
                Toast.makeText(this, "Регистрация не удалась", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String username, String email, String userId, String imageUrl) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("userName", username);
        map.put("email", email);
        map.put("imageUrl", imageUrl);

        reference.setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }
}
