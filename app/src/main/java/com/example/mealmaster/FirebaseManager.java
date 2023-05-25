package com.example.mealmaster;

import com.example.mealmaster.model.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private DatabaseReference mDatabase;

    public FirebaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveRecipe(Recipe recipe) {
        mDatabase.child("recipes").child(String.valueOf(recipe.getId())).setValue(recipe);
    }
}
