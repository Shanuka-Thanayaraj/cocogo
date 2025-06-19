package com.s23010188.cocogo;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<CoconutPost> postList;

    public PostAdapter(List<CoconutPost> postList) {
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        CoconutPost post = postList.get(position);
        holder.quantityTextView.setText("Quantity: " + post.getQuantity());
        holder.priceTextView.setText("Price: $" + post.getPrice());
        holder.placeTextView.setText("Place: " + post.getPlace());
        holder.addressTextView.setText("Address: " + post.getAddress());
        holder.contactTextView.setText("Contact: " + post.getContact());
        holder.locationTextView.setText("Location: " + post.getLocation());
        holder.checkNameTextView.setText("Check Name: " + post.getCheckName());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView quantityTextView, priceTextView, placeTextView, addressTextView, contactTextView, locationTextView, checkNameTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            placeTextView = itemView.findViewById(R.id.placeTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            checkNameTextView = itemView.findViewById(R.id.checkNameTextView);
        }
    }
}