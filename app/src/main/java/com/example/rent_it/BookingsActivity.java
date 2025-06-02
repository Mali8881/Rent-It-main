package com.example.rent_it;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.BookingAdapter;
import com.example.rent_it.Model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        recyclerView = findViewById(R.id.recycler_bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        recyclerView.setAdapter(bookingAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadBookings();
    }

    private void loadBookings() {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");

        bookingsRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookingList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Booking booking = snap.getValue(Booking.class);
                            if (booking != null) {
                                bookingList.add(booking);
                                Log.d("BookingsCount", "Loaded: " + bookingList.size());
                                for (Booking b : bookingList) {
                                    Log.d("Booking", b.getBookingId() + " | " + b.getUserId());
                                }

                            }
                        }
                        bookingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Можно показать Toast здесь при ошибке
                    }
                });
    }

}
