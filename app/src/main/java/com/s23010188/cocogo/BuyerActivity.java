package com.s23010188.cocogo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BuyerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListingAdapter adapter;
    private List<Listing> listingList;
    private FirebaseFirestore firestoreDb;
    private static final String TAG = "BuyerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);

        recyclerView = findViewById(R.id.recyclerViewListings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listingList = new ArrayList<>();
        adapter = new ListingAdapter(this, listingList);
        recyclerView.setAdapter(adapter);

        firestoreDb = FirebaseFirestore.getInstance();
        loadListings();
    }

    private void loadListings() {
        CollectionReference listingsRef = firestoreDb.collection("listings");

        listingsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshots != null) {
                listingList.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Listing listing = doc.toObject(Listing.class);
                    listingList.add(listing);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
