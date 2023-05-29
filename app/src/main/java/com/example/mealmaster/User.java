package com.example.mealmaster;

public class User {

    public String id;
    public String password;
    public String email;

    private boolean isFavorite;

    public User() {
        // Constructeur par défaut requis pour les appels de DataSnapshot.getValue(User.class)
    }

    public User(String password, String email) {
        this.password = password;
        this.email = email;
        this.isFavorite = false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}

