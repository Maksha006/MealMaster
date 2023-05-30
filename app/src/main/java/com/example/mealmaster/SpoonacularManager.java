package com.example.mealmaster;

import android.content.Context;
import android.view.View;

import com.example.mealmaster.Listeners.InstructionsListener;
import com.example.mealmaster.Listeners.RecipeDetailsListener;
import com.example.mealmaster.Listeners.SpoonacularResponseListener;
import com.example.mealmaster.model.InstructionsResponse;
import com.example.mealmaster.model.Recipe;
import com.example.mealmaster.model.RecipeDetailsResponses;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SpoonacularManager {
    private static final String BASE_URL = "https://api.spoonacular.com/";
    private static final String BASE_URL2 = "https://api.apilayer.com/spoonacular/";
    View view;

    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    Retrofit retrofit2 = new Retrofit.Builder()
            .baseUrl(BASE_URL2)
            .addConverterFactory(GsonConverterFactory.create())
            .build();



    public SpoonacularManager(Context context) {
        this.context = context;
    }

    public void getCategoryRandomRecipes(SpoonacularResponseListener listener, String tags) {
        CallRandomRecipe callRandomRecipe = retrofit.create(CallRandomRecipe.class);
        Call<RandomSpoonacularResponse> call = callRandomRecipe.callRandomRecipe(context.getString(R.string.api_key), "25",tags);
        call.enqueue(new Callback<RandomSpoonacularResponse>() {
            @Override
            public void onResponse(Call<RandomSpoonacularResponse> call, Response<RandomSpoonacularResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                // Save recipes to Firebase
                FirebaseManager firebaseManager = new FirebaseManager();
                for (Recipe recipe : response.body().recipes) {
                    firebaseManager.saveRecipe(recipe);
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomSpoonacularResponse> call, Throwable t) {
                listener.didError(t.getMessage());

            }
        });
    }

    public void getRecipeDetails(RecipeDetailsListener listener, int id){
        CallRecipesDetails callRecipesDetails = retrofit2.create(CallRecipesDetails.class);
        Call<RecipeDetailsResponses> call = callRecipesDetails.callRecipesDetails(id,context.getString(R.string.api_key1));
        call.enqueue(new Callback<RecipeDetailsResponses>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponses> call, Response<RecipeDetailsResponses> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RecipeDetailsResponses> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getInstructionsRecipes(InstructionsListener listener, int id){
        CallInstructionsRecipes callInstructions = retrofit.create(CallInstructionsRecipes.class);
        Call<List<InstructionsResponse>> call = callInstructions.CallInstructionsRecipes(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<List<InstructionsResponse>>() {
            @Override
            public void onResponse(Call<List<InstructionsResponse>> call, Response<List<InstructionsResponse>> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didRecipeFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<InstructionsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    private interface CallRandomRecipe{
        @GET("recipes/random")
        Call<RandomSpoonacularResponse>callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") String tags
        );
    }

    private interface CallRecipesDetails{
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponses>callRecipesDetails(
                @Path("id") int id,
                @Query("apikey") String apikey
        );
    }

    private interface CallInstructionsRecipes{
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsResponse>>CallInstructionsRecipes(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }
}
