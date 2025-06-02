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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin;
    private TextView gotoRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        gotoRegister = findViewById(R.id.goto_register);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();

            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(LoginActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(txt_email, txt_password);
            }
        });

        gotoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String role = snapshot.child("role").getValue(String.class);

                        if ("admin".equals(role)) {
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class)); // Замени на свой класс
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



}
