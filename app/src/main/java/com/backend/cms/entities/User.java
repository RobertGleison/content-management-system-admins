package com.backend.cms.entities;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 3L;
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
