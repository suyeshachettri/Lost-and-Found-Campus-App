package com.example.campuslostfound;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ItemsFragment extends Fragment {

    private static final String ARG_TYPE = "type";

    RecyclerView recyclerView;
    ArrayList<Item> itemList;
    ArrayList<Item> fullList;
    ItemAdapter adapter;
    FirebaseFirestore db;
    String type;

    EditText etSearch;
    Button btnAll, btnActive, btnResolved;

    public ItemsFragment() {}

    public static ItemsFragment newInstance(String type) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        fullList = new ArrayList<>();
        adapter = new ItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        etSearch = view.findViewById(R.id.etSearch);
        btnAll = view.findViewById(R.id.btnAll);
        btnActive = view.findViewById(R.id.btnActive);
        btnResolved = view.findViewById(R.id.btnResolved);

        db = FirebaseFirestore.getInstance();

        setupSearchAndFilters();

        loadItems();
    }

    private void loadItems() {
        db.collection("items")
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    itemList.clear();
                    fullList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Item item = doc.toObject(Item.class);
                        item.id = doc.getId();

                        itemList.add(item);
                        fullList.add(item);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setupSearchAndFilters() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString(), "all");
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        btnAll.setOnClickListener(v ->
                filterList(etSearch.getText().toString(), "all"));

        btnActive.setOnClickListener(v ->
                filterList(etSearch.getText().toString(), "active"));

        btnResolved.setOnClickListener(v ->
                filterList(etSearch.getText().toString(), "resolved"));
    }

    private void filterList(String text, String filterType) {

        ArrayList<Item> filteredList = new ArrayList<>();

        for (Item item : fullList) {

            boolean matchesSearch =
                    (item.name != null && item.name.toLowerCase().contains(text.toLowerCase())) ||
                            (item.location != null && item.location.toLowerCase().contains(text.toLowerCase()));

            boolean matchesFilter;

            if ("active".equals(filterType)) {
                matchesFilter = "active".equals(item.status);
            } else if ("resolved".equals(filterType)) {
                matchesFilter = item.status != null && !item.status.equals("active");
            } else {
                matchesFilter = true;
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(item);
            }
        }

        adapter = new ItemAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }
}