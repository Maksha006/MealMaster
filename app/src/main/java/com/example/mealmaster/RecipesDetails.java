package com.example.mealmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmaster.Adapter.IngredientsAdapter;
import com.example.mealmaster.Listeners.RecipeDetailsListener;
import com.example.mealmaster.fragment.HomeFragment;
import com.example.mealmaster.model.RecipeDetailsResponses;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecipesDetails extends AppCompatActivity {

    int id;
    TextView tv_meal_summary, tv_time,tv_people,tv_mealType;
    CollapsingToolbarLayout ct_meal_name;
    ImageView im_meal_image;
    RecyclerView recycler_meal_ingredients;
    SpoonacularManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;

    RecipeDetailsResponses response;
    ImageView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_details);

        btnback = findViewById(R.id.btnBack);

        findViews();

        id = Integer.parseInt(getIntent().getStringExtra("id"));
        manager = new SpoonacularManager(this);
        manager.getRecipeDetails(recipeDetailsListener,id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details...");
        dialog.show();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipesDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void findViews() {
        ct_meal_name = findViewById(R.id.collapsing_toolbar);
        tv_time = findViewById(R.id.tvTime);
        tv_people = findViewById(R.id.tv_people);
        tv_mealType = findViewById(R.id.tv_mealType);
        tv_meal_summary = findViewById(R.id.tvSummary);
        im_meal_image = findViewById(R.id.meal_image);
        recycler_meal_ingredients = findViewById(R.id.meal_ingredients);
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponses response, String message) {
            dialog.dismiss();
            ct_meal_name.setTitle(response.title);
            tv_meal_summary.setText(response.summary);
            Picasso.get().load(response.image).into(im_meal_image);

            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipesDetails.this,LinearLayoutManager.HORIZONTAL,false));
            ingredientsAdapter = new IngredientsAdapter(RecipesDetails.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipesDetails.this,message,Toast.LENGTH_SHORT).show();
        }
    };
}