package com.example.mealmaster.Listeners;

import com.example.mealmaster.RandomSpoonacularResponse;
import com.example.mealmaster.model.Recipe;

public interface CategoryResponseListener {

    void didFetch(Recipe response, String message);
    void didError(String message);
}
