package com.example.mealmaster.Listeners;

import com.example.mealmaster.RandomSpoonacularResponse;

public interface SpoonacularResponseListener {
        void didFetch(RandomSpoonacularResponse response, String message);
        void didError(String message);
}

