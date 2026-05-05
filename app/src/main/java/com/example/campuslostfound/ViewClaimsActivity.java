package com.example.campuslostfound;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class ViewClaimsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ClaimAdapter adapter;
    ArrayList<Claim> claimList;
    FirebaseFirestore db;
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_claims);

        recyclerView = findViewById(R.id.recyclerClaims);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        claimList = new ArrayList<>();
        adapter = new ClaimAdapter(this, claimList, getIntent().getStringExtra("id"));

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        itemId = getIntent().getStringExtra("id");

        loadClaims();
    }

    private void loadClaims() {
        db.collection("items")
                .document(itemId)
                .collection("claims")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    claimList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Claim claim = doc.toObject(Claim.class);
                        claim.id = doc.getId();
                        claimList.add(claim);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}