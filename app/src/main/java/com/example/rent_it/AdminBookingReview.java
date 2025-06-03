package com.example.rent_it;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rent_it.Model.Booking;
import com.example.rent_it.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminBookingReview extends AppCompatActivity {

    private TextView tvDetails;
    private Button btnApprove, btnReject, btnContact;
    private String bookingId, postOwnerId, requesterId, postId;
    private String ownerPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking_review);

        tvDetails = findViewById(R.id.tvBookingDetails);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        btnContact = findViewById(R.id.btnContact);

        if (getIntent().hasExtra("bookingId")) {
            bookingId = getIntent().getStringExtra("bookingId");
            loadBookingDetails();
        }

        btnApprove.setOnClickListener(v -> approveBooking());
        btnReject.setOnClickListener(v -> rejectBooking());
        btnContact.setOnClickListener(v -> openWhatsApp());
    }
    private void loadBookingDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings").child(bookingId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Booking booking = snapshot.getValue(Booking.class);
                if (booking != null) {
                    requesterId = booking.getUserId();
                    postId = booking.getPostId();

                    String message = booking.getMessage();
                    if (message == null || message.isEmpty()) {
                        message = "Сообщение не указано";
                    }

                    tvDetails.setText("Запрос от: " + requesterId +
                            "\nID поста: " + postId +
                            "\nДаты: " + booking.getStartDate() + " - " + booking.getEndDate() +
                            "\nСообщение: " + message);

                    getPostOwnerInfo(postId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void getPostOwnerInfo(String postId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(postOwnerId);
        userRef.child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone = snapshot.getValue(String.class);
                if (phone != null) {
                    String url = "https://wa.me/" + phone.replace("+", "") +
                            "?text=Здравствуйте! Я получил вашу заявку на бронирование.";
                    btnContact.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    });
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void approveBooking() {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings").child(bookingId);
        ref.child("status").setValue("подтверждено");
        ref.child("approvedAt").setValue(timestamp);

        FirebaseDatabase.getInstance().getReference("Notifications")
                .child(requesterId).push()
                .setValue("✅ Ваша заявка подтверждена. Свяжитесь с владельцем жилья.");

        Toast.makeText(this, "Бронирование подтверждено", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void rejectBooking() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings").child(bookingId);
        ref.child("status").setValue("отклонено");

        FirebaseDatabase.getInstance().getReference("Notifications")
                .child(requesterId).push()
                .setValue("❌ К сожалению, ваша заявка отклонена.");

        Toast.makeText(this, "Бронирование отклонено", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openWhatsApp() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(postOwnerId);
        userRef.child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phoneNumber = snapshot.getValue(String.class);
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    openWhatsAppWithPhone(phoneNumber);
                } else {
                    Toast.makeText(AdminBookingReview.this, "Номер телефона не найден", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void openWhatsAppWithPhone(String phoneNumber) {
        // Убедись, что номер без "+" и пробелов (например: 996707123456)
        String url = "https://wa.me/" + phoneNumber + "?text=Здравствуйте! По поводу вашей публикации...";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
