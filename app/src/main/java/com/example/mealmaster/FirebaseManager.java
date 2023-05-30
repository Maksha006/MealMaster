package com.example.mealmaster;

import com.example.mealmaster.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {

    private DatabaseReference mDatabase;

    public FirebaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveRecipe(Recipe recipe) {
        mDatabase.child("recipes").child(String.valueOf(recipe.getId())).setValue(recipe);
    }

    public void setRecipeAsFeatured(String recipeId) {
        DatabaseReference recipeRef = mDatabase.child("recipes").child(recipeId).child("featured");
        recipeRef.setValue(true);
    }

    public void setRecipe(String recipeId) {
        DatabaseReference recipeRef = mDatabase.child("recipes").child(recipeId);
        recipeRef.setValue(true);
    }

}

