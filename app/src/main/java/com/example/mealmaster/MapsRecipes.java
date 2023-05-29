package com.example.mealmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.Adapter.MapsRecipeAdapter;
import com.example.mealmaster.Listeners.RecipeClickListener;
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

public class MapsRecipes extends AppCompatActivity {
    ProgressDialog dialog;
    RecyclerView maps_recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MapsRecipeAdapter mapsRecipeAdapter;
    ImageView im_maps_meal_image;
    TextView tv_maps_meal_name;
    private static final String API_KEY = "af3b71ca41664ff586770e97ce55e795";

    private List<Recipe> recipeList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_recipe);

        findViewsById();

        List<String> cuisineTypes = getIntent().getStringArrayListExtra("CuisineTypes");
        new SpoonacularRecipeRequest().execute(cuisineTypes.toArray(new String[0]));
        updateTitle(cuisineTypes);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.show();

        List<Recipe> recipes = (List<Recipe>) getIntent().getSerializableExtra("recipes");
        if (recipes != null) {
            dialog.dismiss();
            mapsRecipeAdapter = new MapsRecipeAdapter(recipes);
            layoutManager = new GridLayoutManager(this,2);
            maps_recyclerView.setLayoutManager(layoutManager);
        }
    }

    private void updateTitle(List<String> cuisineTypes) {
        StringBuilder tagsBuilder = new StringBuilder();
        for (String tag : cuisineTypes) {
            tagsBuilder.append(tag).append(", ");
        }

        String tagsString = tagsBuilder.toString();
        if (tagsString.endsWith(", ")) {
            tagsString = tagsString.substring(0, tagsString.length() - 2);
        }

        TextView textViewCountry = findViewById(R.id.textView2);
        textViewCountry.setText(tagsString); // Mettez à jour le titre avec les tags
    }

    public class SpoonacularRecipeRequest extends AsyncTask<String, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(String... tags) {
            List<Recipe> recipes = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spoonacular.com/recipes/random").newBuilder();
            urlBuilder.addQueryParameter("apiKey", API_KEY);
            urlBuilder.addQueryParameter("number", "25");

            // Ajoutez tous les tags à la requête
            for (String tag : tags) {
                urlBuilder.addQueryParameter("cuisine", tag);
            }

            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonResult = response.body().string();
                JSONObject jsonObject  = new JSONObject(jsonResult);
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");

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
                maps_recyclerView = findViewById(R.id.maps_recyclerView);
                mapsRecipeAdapter = new MapsRecipeAdapter(recipeList);
                layoutManager = new GridLayoutManager(MapsRecipes.this,2);
                maps_recyclerView.setLayoutManager(layoutManager);
                maps_recyclerView.setAdapter(mapsRecipeAdapter);
            }
        }
    }
    private void findViewsById(){

        maps_recyclerView = findViewById(R.id.maps_recyclerView);
        im_maps_meal_image = findViewById(R.id.maps_dish_image);
        tv_maps_meal_name = findViewById(R.id.maps_dish_name);
    }
}
