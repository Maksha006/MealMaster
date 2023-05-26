package com.example.mealmaster;

import com.example.mealmaster.model.Recipe;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes").child(recipeId);

        Map<String, Object> recipeUpdates = new HashMap<>();
        recipeUpdates.put("featured", true);

        myRef.updateChildren(recipeUpdates);
    }

}
