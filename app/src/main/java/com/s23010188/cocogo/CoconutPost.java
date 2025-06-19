package com.s23010188.cocogo;

public class CoconutPost {
    private String quantity;
    private double price;
    private String place;
    private String address;
    private String contact;
    private String location;
    private String checkName;

    // Required default constructor for Firebase
    public CoconutPost() {}

    public CoconutPost(String quantity, double price, String place, String address, String contact, String location, String checkName) {
        this.quantity = quantity;
        this.price = price;
        this.place = place;
        this.address = address;
        this.contact = contact;
        this.location = location;
        this.checkName = checkName;
    }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCheckName() { return checkName; }
    public void setCheckName(String checkName) { this.checkName = checkName; }
}
