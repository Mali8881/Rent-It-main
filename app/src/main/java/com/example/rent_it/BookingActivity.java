package com.example.rent_it;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rent_it.Model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.BreakIterator;
import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private TextView tvStartDate, tvEndDate;
    private Button btnBook;
    private String startDate = "", endDate = "", postId = "";

    EditText etMessage = findViewById(R.id.etMessage);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        etMessage = findViewById(R.id.etMessage);


        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnBook = findViewById(R.id.btnBook);

        if (getIntent().hasExtra("postId")) {
            postId = getIntent().getStringExtra("postId");
        }

        tvStartDate.setOnClickListener(v -> pickDate(true));
        tvEndDate.setOnClickListener(v -> pickDate(false));

        btnBook.setOnClickListener(v -> bookNow());
    }

    private void pickDate(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                BookingActivity.this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    if (isStartDate) {
                        startDate = date;
                        tvStartDate.setText("Начало: " + date);
                    } else {
                        endDate = date;
                        tvEndDate.setText("Окончание: " + date);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void bookNow() {
        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Выберите даты", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookingId = FirebaseDatabase.getInstance().getReference("Bookings").push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String message = etMessage.getText().toString().trim();

        Booking booking = new Booking(
                bookingId,
                postId,
                userId,
                startDate,
                endDate,
                "ожидание",
                message
        );


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings").child(bookingId);
        ref.setValue(booking).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Заявка отправлена", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Ошибка бронирования: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
