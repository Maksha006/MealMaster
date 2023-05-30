package com.example.mealmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmaster.Adapter.IngredientsAdapter;
import com.example.mealmaster.Adapter.InstructionsAdapter;
import com.example.mealmaster.Adapter.MapsRecipeAdapter;
import com.example.mealmaster.Listeners.InstructionsListener;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.Listeners.RecipeDetailsListener;
import com.example.mealmaster.fragment.HomeFragment;
import com.example.mealmaster.model.InstructionsResponse;
import com.example.mealmaster.model.Recipe;
import com.example.mealmaster.model.RecipeDetailsResponses;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipesDetails extends AppCompatActivity {

    int id;

    int searchId;

    FirebaseAuth firebaseAuth;

    TextView tv_meal_summary, tv_time,tv_people,tv_mealType;
    CollapsingToolbarLayout ct_meal_name;
    ImageView im_meal_image;
    RecyclerView recycler_meal_ingredients,recycler_dish_instructons;

    SpoonacularManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;

    private RecipeDetailsResponses currentRecipe;
    FloatingActionButton fbFavorite;

    InstructionsAdapter instructionsAdapter;

    ImageView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_details);

        firebaseAuth = FirebaseAuth.getInstance();

        findViews();

       // String listId = getIntent().getStringExtra("recipeId");

       // searchId = Integer.parseInt(listId);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String mapIdString = extras.getString("MapsRecipeId");
            String idString = extras.getString("id");
            String searchId = extras.getString("recipeId");
            String searchListId = extras.getString("searchRecipeId");
            if (mapIdString != null) {
                id = Integer.parseInt(mapIdString);
            }else if (idString != null) {
                id = Integer.parseInt(idString);
            } else if (searchId != null) {
                id =  Integer.parseInt(searchId);
            }else if (searchListId != null) {
                id =  Integer.parseInt(searchListId);
            }else {
                Toast.makeText(this, "aucun id n'est présent", Toast.LENGTH_SHORT).show();
            }
            manager = new SpoonacularManager(this);
            manager.getRecipeDetails(recipeDetailsListener, id);
            manager.getInstructionsRecipes(instructionsListener,id);
            dialog = new ProgressDialog(this);
            dialog.setTitle("Loading Details...");
            dialog.show();
        }

        fbFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmptyFavoritesNode();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipesDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createEmptyFavoritesNode() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Firebase", user.getUid());
        System.out.println();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference favoritesRef = ref.child("Users").child(uid).child("favorites");
            // Créer un noeud "favorites" vide
            favoritesRef.setValue(null);
        }
    }

    private void addToFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("Firebase", "User is logged in. Attempting to write to Firebase");
            // Get a reference to the user's favorites in the database
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid())
                    .child("favorites");

            favoritesRef.push().setValue(currentRecipe, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Log.e("Firebase", "Data could not be saved " + error.getMessage());
                    } else {
                        Log.d("Firebase", "Data saved successfully.");
                    }
                }
            });
        } else {
            Log.d("Firebase", "User is not logged in.");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void findViews() {
        btnback = findViewById(R.id.btnBack);
        fbFavorite = findViewById(R.id.fbFavorite);
        ct_meal_name = findViewById(R.id.collapsing_toolbar);
        tv_time = findViewById(R.id.tvTime);
        tv_people = findViewById(R.id.tv_people);
        tv_mealType = findViewById(R.id.tv_mealType);
        tv_meal_summary = findViewById(R.id.tvSummary);
        im_meal_image = findViewById(R.id.meal_image);
        recycler_meal_ingredients = findViewById(R.id.meal_ingredients);
        recycler_dish_instructons = findViewById(R.id.recycler_dish_instructons);
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

    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didRecipeFetch(List<InstructionsResponse> response, String message) {
            recycler_dish_instructons.setHasFixedSize(true);
            recycler_dish_instructons.setLayoutManager(new LinearLayoutManager(RecipesDetails.this,LinearLayoutManager.VERTICAL,false));
            instructionsAdapter = new InstructionsAdapter(RecipesDetails.this,response);
            recycler_dish_instructons.setAdapter(instructionsAdapter);

        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipesDetails.this,message,Toast.LENGTH_SHORT).show();
        }
    };
}