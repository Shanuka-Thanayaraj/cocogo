package com.s23010188.cocogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BuyerDashboardActivity extends AppCompatActivity {

    private Button postAreaBtn, locationTrackBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        postAreaBtn = findViewById(R.id.postAreaButton);
        locationTrackBtn = findViewById(R.id.locationTrackButton);
        logoutBtn = findViewById(R.id.logoutButton);

        postAreaBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BuyerDashboardActivity.this, BuyerActivity.class);
            startActivity(intent);
        });

        locationTrackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BuyerDashboardActivity.this, MapActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(BuyerDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
