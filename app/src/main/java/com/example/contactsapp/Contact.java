package com.example.contactsapp;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String imagePath;

    public Contact(String name, String email, String phone, String imagePath) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImagePath() {
        return imagePath;
    }
}
