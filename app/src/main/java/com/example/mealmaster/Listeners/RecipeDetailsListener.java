package com.example.mealmaster.Listeners;

import com.example.mealmaster.model.RecipeDetailsResponses;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponses response, String message);
    void didError(String message);
}
