package com.example.mealmaster.Listeners;

import com.example.mealmaster.model.InstructionsResponse;

import java.util.List;

public interface InstructionsListener {
    void didRecipeFetch(List<InstructionsResponse> response, String message);
    void didError(String message);
}
