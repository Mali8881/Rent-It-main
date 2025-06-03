package com.example.rent_it.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent_it.Adapter.PostAdapter;
import com.example.rent_it.BookingsActivity;
import com.example.rent_it.Model.Post;
import com.example.rent_it.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Intent;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Post> postList = new ArrayList<>();
    private List<Post> filteredList = new ArrayList<>();
    private PostAdapter postAdapter;
    private DatabaseReference postsRef;
    private EditText editTextSearch;
    private ImageView ivFilter;

    // --- Параметры фильтра, которые задаются через BottomSheet ---
    private String selectedType = "Все";
    private String selectedHeating = "Все";
    private String selectedRooms = "Все";
    private String selectedCity = "";
    private String priceMin = "", priceMax = "", areaMin = "", areaMax = "";
    private boolean wifiOnly = false;

    private String searchQuery = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        CardView cardMyBookings = view.findViewById(R.id.card_my_bookings);
        cardMyBookings.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), BookingsActivity.class));
        });

        editTextSearch = view.findViewById(R.id.editText_search);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), filteredList);
        recyclerView.setAdapter(postAdapter);

        ivFilter = view.findViewById(R.id.ivFilter);

        postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        // --- Поиск ---
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterPosts();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- Клик по иконке фильтра ---
        ivFilter.setOnClickListener(v -> {
            FilterBottomSheet filterSheet = new FilterBottomSheet();

            // Передать текущие значения фильтра (если уже применялись)
            Bundle args = new Bundle();
            args.putString("city", selectedCity);
            args.putString("type", selectedType);
            args.putString("rooms", selectedRooms);
            args.putString("priceMin", priceMin);
            args.putString("priceMax", priceMax);
            args.putString("areaMin", areaMin);
            args.putString("areaMax", areaMax);
            args.putString("heating", selectedHeating);
            args.putBoolean("wifiOnly", wifiOnly);
            filterSheet.setArguments(args);

            filterSheet.setOnFilterAppliedListener(data -> {
                // Получаем параметры фильтра из диалога
                selectedCity = data.city;
                selectedType = data.type;
                selectedRooms = data.rooms;
                priceMin = data.priceMin;
                priceMax = data.priceMax;
                areaMin = data.areaMin;
                areaMax = data.areaMax;
                selectedHeating = data.heating;
                wifiOnly = data.wifi;
                filterPosts();
            });

            filterSheet.show(getParentFragmentManager(), filterSheet.getTag());
        });

        loadAllPosts();
        return view;
    }

    private void loadAllPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    if (post != null) postList.add(post);
                }
                filterPosts();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterPosts() {
        filteredList.clear();
        for (Post post : postList) {
            boolean matchesType = selectedType.equals("Все") || selectedType.equals(post.getType());
            boolean matchesHeating = selectedHeating.equals("Все") || selectedHeating.equals(post.getHeating());
            boolean matchesRooms = selectedRooms.equals("Все") || selectedRooms.equals(post.getBedrooms());
            boolean matchesCity = selectedCity.isEmpty() || (post.getLocation() != null && post.getLocation().toLowerCase().contains(selectedCity.toLowerCase()));
            boolean matchesSearch = searchQuery.isEmpty()
                    || (post.getTitle() != null && post.getTitle().toLowerCase().contains(searchQuery.toLowerCase()))
                    || (post.getLocation() != null && post.getLocation().toLowerCase().contains(searchQuery.toLowerCase()));
            boolean matchesPriceMin = priceMin.isEmpty() || (post.getPrice() != null && !post.getPrice().isEmpty() && Integer.parseInt(post.getPrice()) >= Integer.parseInt(priceMin));
            boolean matchesPriceMax = priceMax.isEmpty() || (post.getPrice() != null && !post.getPrice().isEmpty() && Integer.parseInt(post.getPrice()) <= Integer.parseInt(priceMax));
            boolean matchesAreaMin = areaMin.isEmpty() || (post.getArea() != null && !post.getArea().isEmpty() && Integer.parseInt(post.getArea()) >= Integer.parseInt(areaMin));
            boolean matchesAreaMax = areaMax.isEmpty() || (post.getArea() != null && !post.getArea().isEmpty() && Integer.parseInt(post.getArea()) <= Integer.parseInt(areaMax));
            boolean matchesWifi = !wifiOnly || (post.getWifi() != null && (
                    post.getWifi().equalsIgnoreCase("есть")
                            || post.getWifi().equalsIgnoreCase("yes")
                            || post.getWifi().equalsIgnoreCase("true")));

            if (matchesType && matchesHeating && matchesRooms &&
                    matchesCity && matchesSearch &&
                    matchesPriceMin && matchesPriceMax && matchesAreaMin && matchesAreaMax &&
                    matchesWifi) {
                filteredList.add(post);
            }
        }
        postAdapter.notifyDataSetChanged();
    }
}
