package com.s23010188.cocogo;

public class Listing {
    private String listingId;
    private String sellerId;
    private String sellerName;
    private int coconutCount;
    private double price;
    private String location;
    private String address;
    private String description;
    private long postedAt;

    public Listing() {}

    public Listing(String sellerId, String sellerName, int coconutCount, double price,
                   String location, String address, String description) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.coconutCount = coconutCount;
        this.price = price;
        this.location = location;
        this.address = address;
        this.description = description;
        this.postedAt = System.currentTimeMillis();
    }

    // All getters and setters...
    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public int getCoconutCount() { return coconutCount; }
    public void setCoconutCount(int coconutCount) { this.coconutCount = coconutCount; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getPostedAt() { return postedAt; }
    public void setPostedAt(long postedAt) { this.postedAt = postedAt; }
}