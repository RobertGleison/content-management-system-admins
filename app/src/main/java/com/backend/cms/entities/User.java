package com.backend.cms.entities;

public class User {
    String email;
    String password;
    // Add other fields you need


    public User(String email, String pass) {
        this.email = email;
        this.password = pass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
