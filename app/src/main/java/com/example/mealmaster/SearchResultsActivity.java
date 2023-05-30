package com.example.mealmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.Adapter.RandomSliderAdapter;
import com.example.mealmaster.Adapter.SearchRecipeAdapter;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultsActivity extends AppCompatActivity {

    private static final String API_KEY = "zRMMlpwqL9NvB6uMn1m8scsccUxIesRv";

    private static final int SEARCH_NUMBER = 25;
    androidx.appcompat.widget.SearchView searchView;
    ImageView im_meal_image;
    TextView tv_meal_name;
    ProgressDialog dialog;
    RecyclerView recyclerView;

    RecipeClickListener clickListener;

    private SearchRecipeAdapter.RecipeFeatureClickListener featureListener;

    private List<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_recipe);

        findViews();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String searchIngredients = s;
                new SpoonacularRecipeRequest().execute(searchIngredients);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String searchIngredients = s;
                new SpoonacularRecipeRequest().execute(searchIngredients);
                return false;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading ...");
        dialog.show();

        List<Recipe> recipes = (List<Recipe>) getIntent().getSerializableExtra("recipes");
        if (recipes != null) {
            dialog.dismiss();
            SearchRecipeAdapter adapter = new SearchRecipeAdapter(SearchResultsActivity.this,recipes,recipeClickListener,featureListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    public class SpoonacularRecipeRequest extends AsyncTask<String, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(String... searchIngredients) {
            List<Recipe> recipes = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.apilayer.com/spoonacular/recipes/complexSearch").newBuilder();
            urlBuilder.addQueryParameter("query", searchIngredients[0]);
            urlBuilder.addQueryParameter("number", String.valueOf(SEARCH_NUMBER));
            urlBuilder.addQueryParameter("apikey", API_KEY);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonResult = response.body().string();
                JSONObject jsonObject  = new JSONObject (jsonResult);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject recipeJson = jsonArray.getJSONObject(i);
                    String id = recipeJson.getString("id");
                    String name = recipeJson.getString("title");
                    String imageURL = recipeJson.getString("image");
                    Recipe recipe = new Recipe(Integer.parseInt(id),name, imageURL);
                    recipes.add(recipe);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return recipes;
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            dialog.dismiss();
            if (recipes != null && recipes.size() > 0){
                recipeList.clear();
                recipeList.addAll(recipes);
                recyclerView = findViewById(R.id.recipe_recycler_view);
                SearchRecipeAdapter adapter = new SearchRecipeAdapter(SearchResultsActivity.this,recipes,recipeClickListener,featureListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultsActivity.this));
                recyclerView.setAdapter(adapter);

                FirebaseManager firebaseManager = new FirebaseManager();
                firebaseManager.saveSearchRecipes(recipes);
            }
        }
    }

    private void findViews(){
        searchView = findViewById(R.id.search_recipes);
        recyclerView = findViewById(R.id.recipe_recycler_view);
        im_meal_image = findViewById(R.id.picture_food);
        tv_meal_name = findViewById(R.id.Dish_title);
    }

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void OnRecipeClicked(String id) {
            Intent intent = new Intent(SearchResultsActivity.this, RecipesDetails.class)
                    .putExtra("searchRecipeId",id);
            Log.d("Recipe",id);
            startActivity(intent);
        }
    };

    private RandomSliderAdapter.RecipeFeatureClickListener featureFavoriteListener = new RandomSliderAdapter.RecipeFeatureClickListener() {
        @Override
        public void onRecipeFeatureClick(String recipeId) {
            FirebaseManager firebaseManager = new FirebaseManager();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Mettre à jour l'attribut isFavorite de la recette
                for (Recipe recipe : recipeList) {
                    if (String.valueOf(recipe.getId()).equals(recipeId)) {
                        if (recipe.isFavorite()) {
                            recipe.setFavorite(false);
                            firebaseManager.removeUserFavoriteRecipe(user.getUid(), recipeId);
                        } else {
                            recipe.setFavorite(true);
                            firebaseManager.saveUserFavoriteRecipe(user.getUid(), recipeId, recipe);
                        }
                        break;
                    }
                }
            } else {
                Toast.makeText(SearchResultsActivity.this, "Vous n'êtes pas connécté , veuillez vous connecter", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
