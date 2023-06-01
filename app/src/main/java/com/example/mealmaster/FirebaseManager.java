package com.example.mealmaster;

import com.example.mealmaster.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    private DatabaseReference mDatabase;

    public FirebaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveRecipe(Recipe recipe) {
        mDatabase.child("recipes").child(String.valueOf(recipe.getId())).setValue(recipe);
    }

    public void saveSearchRecipes(List<Recipe> recipes) {
        DatabaseReference recipeRef = mDatabase.child("recipes").child("searchRecipes");
        for (Recipe recipe : recipes) {
            recipeRef.child(String.valueOf(recipe.getId())).setValue(recipe);
        }
    }


    public void setRecipeAsFeatured(String recipeId) {
        DatabaseReference recipeRef = mDatabase.child("recipes").child(recipeId).child("featured");
        recipeRef.setValue(true);
    }

    public void setRecipe(Recipe recipe) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid())
                    .child("favorites")
                    .child(String.valueOf(recipe.getId()));
            userRef.setValue(recipe);
        }
    }

    public void saveUserFavoriteRecipe(String userId, String recipeId, Recipe recipe) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userId)
                    .child("favorites")
                    .child(recipeId);

            userRef.setValue(recipe);
        }
    }

    public void removeUserFavoriteRecipe(String userId, String recipeId, Recipe recipe) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userId)
                    .child("favorites")
                    .child(recipeId);

            userRef.removeValue();
        }
    }
}

