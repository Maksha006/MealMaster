package com.example.mealmaster.model;

import com.google.gson.annotations.SerializedName;

public class Recipe {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;

    private boolean featured;

    public Recipe() {
    }

    public Recipe(int id, String title, String image,boolean featured) {
        this.title = title;
        this.image = image;
        this.featured = featured;
    }

    public boolean isFeatured() {
        return featured;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }


    public void setId(int id) {this.id = id;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

}
