package com.example.rent_it.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rent_it.Adapter.UserAdapter;
import com.example.rent_it.Model.User;
import com.example.rent_it.R;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private final ArrayList<User> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(getContext(), userList, new UserAdapter.OnUserActionListener() {
            @Override public void onUserClicked(User user) {}

            @Override public void onEditClicked(User user) {
                Toast.makeText(getContext(), "Редактировать: " + user.getFullName(), Toast.LENGTH_SHORT).show();
            }

            @Override public void onDeleteClicked(User user) {
                FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).removeValue();
                Toast.makeText(getContext(), "Удалено: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                loadUsers();
            }

            @Override public void onSendMessageClicked(User user) {
                Toast.makeText(getContext(), "Сообщение: " + user.getFullName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(userAdapter);
        loadUsers();
        return view;
    }

    private void loadUsers() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            if (user != null) userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
