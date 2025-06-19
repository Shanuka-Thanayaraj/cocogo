package com.s23010188.cocogo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SellerActivity extends AppCompatActivity {

    private static final String TAG = "SellerActivity";
    private EditText etCoconutCount, etPrice, etLocation, etAddress, etDescription;
    private Button btnPostListing;

    private FirebaseUser currentUser;
    private DatabaseReference realtimeDbRef;
    private FirebaseFirestore firestoreDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        etCoconutCount = findViewById(R.id.etCoconutCount);
        etPrice = findViewById(R.id.etPrice);
        etLocation = findViewById(R.id.etLocation);
        etAddress = findViewById(R.id.etAddress);
        etDescription = findViewById(R.id.etDescription);
        btnPostListing = findViewById(R.id.btnPostListing);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        realtimeDbRef = FirebaseDatabase.getInstance().getReference("users");
        firestoreDb = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnPostListing.setOnClickListener(v -> checkUserRoleAndPost());
    }

    private void checkUserRoleAndPost() {
        realtimeDbRef.child(currentUser.getUid()).child("role")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String userRole = snapshot.getValue(String.class);
                    if ("Seller".equalsIgnoreCase(userRole)) {
                        postListingToFirestore();
                    } else {
                        Toast.makeText(this, "Seller privileges required", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to read role", e);
                    Toast.makeText(this, "Error reading user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void postListingToFirestore() {
        String countStr = etCoconutCount.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Added description to mandatory fields check
        if (countStr.isEmpty() || priceStr.isEmpty() || location.isEmpty() || address.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int count = Integer.parseInt(countStr);
            double price = Double.parseDouble(priceStr);

            if (count <= 0 || price <= 0) {
                Toast.makeText(this, "Values must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            String sellerName = currentUser.getDisplayName();
            if (sellerName == null) sellerName = currentUser.getEmail();

            Map<String, Object> listing = new HashMap<>();
            listing.put("sellerId", currentUser.getUid());
            listing.put("sellerName", sellerName);
            listing.put("coconutCount", count);
            listing.put("price", price);
            listing.put("location", location);
            listing.put("address", address);
            listing.put("description", description);
            listing.put("postedAt", System.currentTimeMillis());

            firestoreDb.collection("listings")
                    .add(listing)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Listing posted!", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Post failed", e);
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etCoconutCount.setText("");
        etPrice.setText("");
        etLocation.setText("");
        etAddress.setText("");
        etDescription.setText("");
        etCoconutCount.requestFocus();
    }
}