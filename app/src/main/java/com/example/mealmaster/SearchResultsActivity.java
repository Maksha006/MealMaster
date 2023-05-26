package com.example.mealmaster;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.Adapter.SearchRecipeAdapter;
import com.example.mealmaster.model.Recipe;

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

    private static final String API_KEY = "af3b71ca41664ff586770e97ce55e795";

    private static final int SEARCH_NUMBER = 25;
    androidx.appcompat.widget.SearchView searchView;
    ImageView im_meal_image;
    TextView tv_meal_name;
    ProgressDialog dialog;
    RecyclerView recyclerView;

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
            SearchRecipeAdapter adapter = new SearchRecipeAdapter(recipes);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    public class SpoonacularRecipeRequest extends AsyncTask<String, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(String... searchIngredients) {
            List<Recipe> recipes = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spoonacular.com/recipes/complexSearch").newBuilder();
            urlBuilder.addQueryParameter("query", searchIngredients[0]);
            urlBuilder.addQueryParameter("number", String.valueOf(SEARCH_NUMBER));
            urlBuilder.addQueryParameter("apiKey", API_KEY);
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
                    String name = recipeJson.getString("title");
                    String imageURL = recipeJson.getString("image");
                    Recipe recipe = new Recipe(name, imageURL);
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
                SearchRecipeAdapter adapter = new SearchRecipeAdapter(recipeList);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultsActivity.this));
                recyclerView.setAdapter(adapter);
                // Cacher le texte "Aucune recette trouvée."
            }
        }
    }

    private void findViews(){
        searchView = findViewById(R.id.search_recipes);
        recyclerView = findViewById(R.id.recipe_recycler_view);
        im_meal_image = findViewById(R.id.picture_food);
        tv_meal_name = findViewById(R.id.Dish_title);
    }
}
