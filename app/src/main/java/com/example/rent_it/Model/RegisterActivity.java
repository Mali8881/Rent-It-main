package com.example.rent_it.Model;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rent_it.MainActivity;
import com.example.rent_it.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, phone;
    Button btnRegister;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        btnRegister = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> {
            String txtEmail = email.getText().toString();
            String txtPassword = password.getText().toString();
            String txtPhone = phone.getText().toString();

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtPhone)) {
                Toast.makeText(RegisterActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else if (txtPassword.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(txtEmail, txtPassword, txtPhone);
            }
        });
    }

    private void registerUser(String email, String password, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userId = firebaseUser.getUid();

                // Сохраняем номер телефона в базу
                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("phone").setValue(phone);

                Toast.makeText(RegisterActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
