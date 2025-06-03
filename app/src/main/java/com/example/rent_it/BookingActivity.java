package com.example.rent_it;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private TextView tvStartDate, tvEndDate;
    private Button btnBook;
    private String postId = "";

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private EditText etMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnBook = findViewById(R.id.btnBook);
        etMessage = findViewById(R.id.etMessage);

        if (getIntent().hasExtra("postId")) {
            postId = getIntent().getStringExtra("postId");
        }

        tvStartDate.setOnClickListener(v -> pickDate(true));
        tvEndDate.setOnClickListener(v -> pickDate(false));

        btnBook.setOnClickListener(v -> bookNow());
    }

    private void pickDate(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;

        new DatePickerDialog(
                BookingActivity.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String formattedDate = dateFormat.format(calendar.getTime());

                    if (isStartDate) {
                        tvStartDate.setText("Начало: " + formattedDate);
                    } else {
                        tvEndDate.setText("Окончание: " + formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void bookNow() {
        String startDate = dateFormat.format(startCalendar.getTime());
        String endDate = dateFormat.format(endCalendar.getTime());
        String message = etMessage.getText().toString().trim();

        // Проверка на пустые даты
        if (tvStartDate.getText().toString().contains("Выбрать") || tvEndDate.getText().toString().contains("Выбрать")) {
            Toast.makeText(this, "Пожалуйста, выберите обе даты", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка логики дат
        if (endCalendar.before(startCalendar)) {
            Toast.makeText(this, "Дата окончания не может быть раньше даты начала", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookingId = FirebaseDatabase.getInstance().getReference("Bookings").push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
