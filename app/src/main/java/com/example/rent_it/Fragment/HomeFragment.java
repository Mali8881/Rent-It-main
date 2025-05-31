package com.example.rent_it.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.PostAdapter;
import com.example.rent_it.Model.Post;
import com.example.rent_it.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Spinner spinnerType, spinnerHeating, spinnerWifi;
    private RecyclerView recyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private DatabaseReference postsRef;

    private String selectedType = "Все";
    private String selectedHeating = "Все";
    private String selectedWifi = "Все";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerType = view.findViewById(R.id.spinner_type);
        spinnerHeating = view.findViewById(R.id.spinner_heating);
        spinnerWifi = view.findViewById(R.id.spinner_wifi);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        setupSpinner(spinnerType, R.array.type_options);
        setupSpinner(spinnerHeating, R.array.heating_options);
        setupSpinner(spinnerWifi, R.array.wifi_options);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = spinnerType.getSelectedItem().toString();
                selectedHeating = spinnerHeating.getSelectedItem().toString();
                selectedWifi = spinnerWifi.getSelectedItem().toString();
                filterPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinnerType.setOnItemSelectedListener(listener);
        spinnerHeating.setOnItemSelectedListener(listener);
        spinnerWifi.setOnItemSelectedListener(listener);

        loadAllPosts(); // Загрузим сначала все
        return view;
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadAllPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filterPosts() {
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post != null) {
                        boolean matchesType = selectedType.equals("Все") || selectedType.equals(post.getType());
                        boolean matchesHeating = selectedHeating.equals("Все") || selectedHeating.equals(post.getHeating());
                        boolean matchesWifi = selectedWifi.equals("Все") ||
                                selectedWifi.equals(post.getWifi());


                        if (matchesType && matchesHeating && matchesWifi) {
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
