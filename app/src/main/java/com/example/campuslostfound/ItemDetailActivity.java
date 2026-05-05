package com.example.campuslostfound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity {

    ImageView detailImage;
    TextView detailName, detailLocation, detailDescription, detailType, detailStatus;
    Button btnMarkReturned, btnClaim;
    Button btnViewClaims;

    FirebaseFirestore db;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailLocation = findViewById(R.id.detailLocation);
        detailDescription = findViewById(R.id.detailDescription);
        detailType = findViewById(R.id.detailType);
        detailStatus = findViewById(R.id.detailStatus);

        btnMarkReturned = findViewById(R.id.btnMarkReturned);
        btnClaim = findViewById(R.id.btnClaim);
        btnViewClaims = findViewById(R.id.btnViewClaims);

        db = FirebaseFirestore.getInstance();

        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String location = getIntent().getStringExtra("location");
        String description = getIntent().getStringExtra("description");
        String type = getIntent().getStringExtra("type");
        String image = getIntent().getStringExtra("image");
        String status = getIntent().getStringExtra("status");
        String ownerId = getIntent().getStringExtra("userId");

        String currentUser = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (currentUser != null && currentUser.equals(ownerId)) {

            btnMarkReturned.setVisibility(View.VISIBLE);
            btnClaim.setVisibility(View.GONE);
            btnViewClaims.setVisibility(View.VISIBLE);

        } else {

            btnMarkReturned.setVisibility(View.GONE);
            btnViewClaims.setVisibility(View.GONE);
            btnClaim.setVisibility(View.VISIBLE);

            if ("lost".equals(type)) {
                btnClaim.setText("I Found This");
            } else {
                btnClaim.setText("Claim Item");
            }
        }

        detailName.setText(name);
        detailLocation.setText("Location: " + location);
        detailDescription.setText("Description: " + description);
        detailType.setText("Type: " + type.toUpperCase());

        if ("lost".equals(type) && "active".equals(status)) {
            detailStatus.setText("🔴 LOST");
            detailStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        } else if ("found".equals(type) && "active".equals(status)) {
            detailStatus.setText("🟢 FOUND");
            detailStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        } else if ("found".equals(status)) {
            detailStatus.setText("🟢 FOUND");
            detailStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        } else if ("returned".equals(status)) {
            detailStatus.setText("⚫ RETURNED");
            detailStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        if (image != null && !image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            detailImage.setImageBitmap(bitmap);
        }

        if (currentUser != null && currentUser.equals(ownerId)) {

            btnMarkReturned.setVisibility(View.VISIBLE);
            btnClaim.setVisibility(View.GONE);

            if ("lost".equals(type)) {
                btnMarkReturned.setText("Mark as Found");
            } else {
                btnMarkReturned.setText("Mark as Returned");
            }

        } else {

            btnMarkReturned.setVisibility(View.GONE);
            btnClaim.setVisibility(View.VISIBLE);

            if ("lost".equals(type)) {
                btnClaim.setText("I Found This");
            } else {
                btnClaim.setText("Claim Item");
            }
        }

        btnMarkReturned.setOnClickListener(v -> markReturned());
        btnClaim.setOnClickListener(v -> showClaimDialog());

        btnViewClaims.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewClaimsActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }

    private void markReturned() {

        String type = getIntent().getStringExtra("type");

        String newStatus;

        if ("lost".equals(type)) {
            newStatus = "found";
        } else {
            newStatus = "returned";
        }

        db.collection("items").document(id)
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void showClaimDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Submit Request");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_claim, null);
        builder.setView(view);

        android.widget.EditText etName = view.findViewById(R.id.etName);
        android.widget.EditText etPhone = view.findViewById(R.id.etPhone);

        builder.setPositiveButton("Submit", (dialog, which) -> {

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveClaim(name, phone);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveClaim(String name, String phone) {

        Map<String, Object> claim = new HashMap<>();
        claim.put("name", name);
        claim.put("phone", phone);
        claim.put("timestamp", FieldValue.serverTimestamp());
        claim.put("status", "pending");

        db.collection("items")
                .document(id)
                .collection("claims")
                .add(claim)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Submitted!", Toast.LENGTH_SHORT).show();
                });
    }
}