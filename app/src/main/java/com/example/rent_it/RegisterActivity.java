package com.example.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button btnRegister;
    private TextView gotoLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_register);
        gotoLogin = findViewById(R.id.goto_login);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> {
            String txt_username = username.getText().toString().trim();
            String txt_email = email.getText().toString().trim();
            String txt_password = password.getText().toString().trim();

            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(txt_username, txt_email, txt_password);
            }
        });

        gotoLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void registerUser(String txt_username, String txt_email, String txt_password) {
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                // 👉 Автоматически назначаем роль в зависимости от email
                String role = txt_email.equals("admin@gmail.com") ? "admin" : "user";

                HashMap<String, Object> map = new HashMap<>();
                map.put("userName", txt_username);
                map.put("email", txt_email);
                map.put("imageUrl", "default");
                map.put("bio", "");
                map.put("role", role);

                reference.setValue(map).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (role.equals("admin")) {
                            startActivity(new Intent(RegisterActivity.this, AdminActivity.class));
                        } else {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        }
                        finish();
                    }
                });

            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
