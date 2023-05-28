package com.example.mealmaster.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Recipe implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;

    @SerializedName("tags")
    private String tags;

    private boolean featured;

    private boolean isFavorite;

    private int vote;

    public Recipe() {
    }

    public Recipe(int id, String title, String image,boolean featured) {
        this.title = title;
        this.image = image;
        this.featured = featured;
        this.isFavorite = false;
    }

    public Recipe(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public Recipe(String title, String image, int vote) {
        this.title = title;
        this.image = image;
        this.vote = vote;
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

    public String getTags() {
        return tags;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setId(int id) {this.id = id;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
