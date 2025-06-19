package com.s23010188.cocogo;

public class UserStore {
    public String email;
    public String userType;

    public UserStore() {
        // Required for Firebase
    }

    public UserStore(String email, String userType) {
        this.email = email;
        this.userType = userType;
    }
}
