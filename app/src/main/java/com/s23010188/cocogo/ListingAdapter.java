package com.s23010188.cocogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private Context context;
    private List<Listing> listingList;

    public ListingAdapter(Context context, List<Listing> listingList) {
        this.context = context;
        this.listingList = listingList;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listing_item, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listingList.get(position);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        holder.tvSellerName.setText(listing.getSellerName());
        holder.tvCoconutCount.setText(numberFormat.format(listing.getCoconutCount()) + " coconuts");
        holder.tvPrice.setText(currencyFormat.format(listing.getPrice()));
        holder.tvLocation.setText(listing.getLocation());
        holder.tvAddress.setText(listing.getAddress());

        if (listing.getDescription() != null && !listing.getDescription().isEmpty()) {
            holder.tvDescription.setText(listing.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        String postedDate = sdf.format(new Date(listing.getPostedAt()));
        holder.tvPostedAt.setText("Posted: " + postedDate);
    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView tvSellerName, tvCoconutCount, tvPrice, tvLocation,
                tvAddress, tvDescription, tvPostedAt;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            tvCoconutCount = itemView.findViewById(R.id.tvCoconutCount);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPostedAt = itemView.findViewById(R.id.tvPostedAt);
        }
    }
}