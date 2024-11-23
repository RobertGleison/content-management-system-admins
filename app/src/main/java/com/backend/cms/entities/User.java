package com.backend.cms.entities;


public class User {
    String email;
    String password;

    public User(String email, String pass) {
        this.email = email;
        this.password = pass;
    }

    public String getPassword() {return password;}
    public String getEmail() {
        return email;
    }
}
