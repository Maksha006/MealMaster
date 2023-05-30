package com.example.mealmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmaster.Adapter.CategoryRecipeAdapter;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.Listeners.SpoonacularResponseListener;

import java.util.List;

public class ListOfRecipes extends AppCompatActivity {


    List<String> tags;
    ImageView im_meal_image;
    TextView tv_meal_name;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    SpoonacularManager manager;

    CategoryRecipeAdapter randomCategoryRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipes);

        findViews();

        String selectedTag = getIntent().getStringExtra("selectedTag");
        manager = new SpoonacularManager(this);
        manager.getCategoryRandomRecipes(spoonacularResponseListener,selectedTag);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Recipes...");
        dialog.show();
    }

    private void findViews() {
        tv_meal_name = findViewById(R.id.DishName_category);
        im_meal_image = findViewById(R.id.image_category_food);
        recyclerView = findViewById(R.id.recycler_search);
    }

    private final SpoonacularResponseListener spoonacularResponseListener = new SpoonacularResponseListener() {
        @Override
        public void didFetch(RandomSpoonacularResponse response, String message) {
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_search);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ListOfRecipes.this));
            randomCategoryRecipesAdapter = new CategoryRecipeAdapter(ListOfRecipes.this,response.recipes,recipeClickListener);
            recyclerView.setAdapter(randomCategoryRecipesAdapter);
        }
        @Override
        public void didError(String message) {
            Toast.makeText(ListOfRecipes.this,message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void OnRecipeClicked(String id) {
            Intent intent = new Intent(ListOfRecipes.this, RecipesDetails.class)
                    .putExtra("recipeId",id);
            startActivity(intent);
        }
    };

}