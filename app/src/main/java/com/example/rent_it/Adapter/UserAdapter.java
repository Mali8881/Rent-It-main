package com.example.rent_it.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Model.User;
import com.example.rent_it.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private String phone;
    private final Context context;
    private final List<User> userList;
    private final OnUserActionListener listener;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public interface OnUserActionListener {
        void onUserClicked(User user);
        void onEditClicked(User user);
        void onDeleteClicked(User user);
        void onSendMessageClicked(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getFullName());

        holder.itemView.setOnClickListener(v -> listener.onUserClicked(user));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClicked(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClicked(user));
        holder.btnMessage.setOnClickListener(v -> listener.onSendMessageClicked(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        Button btnEdit, btnDelete, btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnMessage = itemView.findViewById(R.id.btn_message);
        }
    }
}
