package com.example.rent_it.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Model.Booking;
import com.example.rent_it.R;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private Context mContext;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.mContext = context;
        this.bookingList = bookings;
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvTitle.setText("ID жилья: " + booking.getPostId());
        holder.tvDate.setText("С " + booking.getStartDate() + " по " + booking.getEndDate());
        holder.tvStatus.setText("Статус: " + booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookingTitle);
            tvDate = itemView.findViewById(R.id.tvBookingDates);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);

        }
    }
}
