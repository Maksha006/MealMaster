package com.example.mealmaster.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealmaster.Adapter.RandomSliderAdapter;
import com.example.mealmaster.FirebaseManager;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.R;
import com.example.mealmaster.RandomSpoonacularResponse;
import com.example.mealmaster.RecipesDetails;
import com.example.mealmaster.RegisterActivity;
import com.example.mealmaster.SpoonacularManager;
import com.example.mealmaster.Listeners.SpoonacularResponseListener;
import com.example.mealmaster.User;
import com.example.mealmaster.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private Context mContext;
    private View rootView;

    Button btVedette;

    FloatingActionButton fbFav;
    FirebaseDatabase db;

    FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;
    Boolean fvrtChecked = false;

    private final String API_KEY = "af3b71ca41664ff586770e97ce55e795";

    private static final int SEARCH_NUMBER = 10;


    //// graphisme (bouton,titre,images) pour le tournoi /////
    ImageView im_recipe1;
    ImageView im_recipe2;

    TextView tv_recipe1;
    TextView tv_recipe2;

    Button bt_vote1;
    Button bt_vote2;

    ////encore des variable pour le tournoi ////
    private Handler handler;
    private Runnable runnable;
   // private TournamentRecipeRequest tournamentRecipeRequest;
    ///////////////////////////////////

    private SliderView sliderView;
    private RandomSliderAdapter imageSliderAdapter;
    ProgressDialog dialog;
    SpoonacularManager manager;
    private RandomSliderAdapter.RecipeFeatureClickListener featureListener;

    private List<Recipe> featuredRecipeList = new ArrayList<>();
    private List<Recipe> RecipeList = new ArrayList<>();

    private int displayedRecipeIndex1;
    private int displayedRecipeIndex2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // graphisme(bouton,titre,images) pour le tournoi //////////////////
        im_recipe1 = rootView.findViewById(R.id.recipeImage1);
        im_recipe2 = rootView.findViewById(R.id.recipeImage2);
        tv_recipe1 = rootView.findViewById(R.id.option1);
        tv_recipe2 = rootView.findViewById(R.id.option2);
        bt_vote1 = rootView.findViewById(R.id.voteButton1);
        bt_vote2 = rootView.findViewById(R.id.voteButton2);
        /////////////////////////////////////////////////////

        List<Recipe> newRecipes = new ArrayList<>();
        newRecipes.add(new Recipe("Thai Pizza", "https://spoonacular.com/recipeImages/663136-312x231.jpg", 0));
        newRecipes.add(new Recipe("Plantain Pizza", "https://spoonacular.com/recipeImages/716300-312x231.jpg", 0));
        newRecipes.add(new Recipe("Zucchini Pizza Boats", "https://spoonacular.com/recipeImages/665769-312x231.jpg", 0));
        newRecipes.add(new Recipe("Pepperoni Pizza Muffins", "https://spoonacular.com/recipeImages/655698-312x231.jpg", 0));
        newRecipes.add(new Recipe("Pittata - Pizza Frittata", "https://spoonacular.com/recipeImages/622598-312x231.jpg", 0));
        newRecipes.add(new Recipe("Easy Cheesy Pizza Casserole", "https://spoonacular.com/recipeImages/641893-312x231.jpg", 0));
        newRecipes.add(new Recipe("Paneer & Fig Pizza", "https://spoonacular.com/recipeImages/654523-312x231.jpg", 0));
        newRecipes.add(new Recipe("Pesto Veggie Pizza", "https://spoonacular.com/recipeImages/655847-312x231.jpg", 0));

        loadRecipesFromFirebase();

        scheduleRecipeLoading();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(mContext);
        dialog.setTitle("Loading...");

        bt_vote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForRecipe(newRecipes.get(displayedRecipeIndex1).getTitle());
            }
        });

        bt_vote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForRecipe(newRecipes.get(displayedRecipeIndex2).getTitle());
            }
        });

        /*manager = new SpoonacularManager(mContext);
        manager.getSpoonacularApi(spoonacularResponseListener);
        dialog.show();*/

        // Bouton favori / vedette
        fbFav = rootView.findViewById(R.id.fbFavorite);
        btVedette = rootView.findViewById(R.id.btn_vedette);

        List<String> recipeIds = new ArrayList<>();
        recipeIds.add("149199");
        recipeIds.add("157375");
        recipeIds.add("631741");
        recipeIds.add("631748");

        setMultipleRecipesAsFeatured(recipeIds);

        getFirebaseRecipes();

        db = FirebaseDatabase.getInstance();
        databaseReference =db.getReference();

        return rootView;
    }

    private class LoadRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            List<Recipe> newRecipes = new ArrayList<>();
            newRecipes.add(new Recipe("Thai Pizza", "https://spoonacular.com/recipeImages/663136-312x231.jpg", 0));
            newRecipes.add(new Recipe("Plantain Pizza", "https://spoonacular.com/recipeImages/716300-312x231.jpg", 0));
            newRecipes.add(new Recipe("Zucchini Pizza Boats", "https://spoonacular.com/recipeImages/665769-312x231.jpg", 0));
            newRecipes.add(new Recipe("Pepperoni Pizza Muffins", "https://spoonacular.com/recipeImages/655698-312x231.jpg", 0));
            newRecipes.add(new Recipe("Pittata - Pizza Frittata", "https://spoonacular.com/recipeImages/622598-312x231.jpg", 0));
            newRecipes.add(new Recipe("Easy Cheesy Pizza Casserole", "https://spoonacular.com/recipeImages/641893-312x231.jpg", 0));
            newRecipes.add(new Recipe("Paneer & Fig Pizza", "https://spoonacular.com/recipeImages/654523-312x231.jpg", 0));
            newRecipes.add(new Recipe("Pesto Veggie Pizza", "https://spoonacular.com/recipeImages/655847-312x231.jpg", 0));
            return newRecipes;
        }

        @Override
        protected void onPostExecute(List<Recipe> newRecipes) {
            onRecipesLoaded(newRecipes);
        }
    }

    private void loadRecipesFromFirebase() {
        new LoadRecipesTask().execute();
    }


    private void scheduleRecipeLoading() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Exécute loadRecipesFromFirebase sur un autre thread
                new LoadRecipesTask().execute();
                // Programmation de l'exécution du Runnable pour qu'il se répète toutes les 5 minutes
                handler.postDelayed(this, 5 * 60 * 1000);
            }
        }, 5 * 60 * 1000); // Démarrer après 5 minutes
    }

    private void onRecipesLoaded(List<Recipe> newRecipes) {
        if (newRecipes.size() >= 2) {
            // Choisissez deux indices aléatoires distincts
            int index1 = new Random().nextInt(newRecipes.size());
            int index2;
            do {
                index2 = new Random().nextInt(newRecipes.size());
            } while (index1 == index2);

            displayedRecipeIndex1 = index1;
            displayedRecipeIndex2 = index2;

            String nameRecipe1 = newRecipes.get(index1).getTitle();
            String nameRecipe2 = newRecipes.get(index2).getTitle();

            String imageUrl1 = newRecipes.get(index1).getImage();
            String imageUrl2 = newRecipes.get(index2).getImage();

            // Utilisez Picasso pour charger les images dans vos ImageView
            Picasso.get().load(imageUrl1).into(im_recipe1);
            Picasso.get().load(imageUrl2).into(im_recipe2);

            tv_recipe1.setText(nameRecipe1);
            tv_recipe2.setText(nameRecipe2);
        }
    }

    private void voteForRecipe(String recipeName) {

        String recipeKey = recipeName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeKey).child("votes");
        recipeRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer votes = mutableData.getValue(Integer.class);
                if (votes == null) {
                    // Si aucun vote n'existe encore, initialisez le compteur à 1
                    votes = 1;
                } else {
                    // Incrémente le nombre de votes
                    votes++;
                }

                // Met à jour le nombre de votes dans Firebase
                mutableData.setValue(votes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    // Le vote a été enregistré avec succès
                    Toast.makeText(mContext, "Vote enregistré !", Toast.LENGTH_SHORT).show();
                } else {
                    // La transaction a échoué
                    Toast.makeText(mContext, "Erreur lors de l'enregistrement du vote.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private RandomSliderAdapter.RecipeFeatureClickListener featureFavoriteListener = new RandomSliderAdapter.RecipeFeatureClickListener() {
        @Override
        public void onRecipeFeatureClick(String recipeId) {
            FirebaseManager firebaseManager = new FirebaseManager();
            firebaseManager.setRecipe(recipeId);

            // Mettre à jour l'attribut isFavorite de la recette
            for (Recipe recipe : RecipeList) {
                if (String.valueOf(recipe.getId()).equals(recipeId)) {
                    recipe.setFavorite(!recipe.isFavorite());
                    firebaseManager.saveRecipe(recipe);
                    break;
                }
            }
        }
    };

    public void setMultipleRecipesAsFeatured(List<String> recipeIds) {
        for (String recipeId : recipeIds) {
            FirebaseManager firebaseManager = new FirebaseManager();
            firebaseManager.setRecipeAsFeatured(recipeId);
        }
    }

    private SpoonacularResponseListener spoonacularResponseListener = new SpoonacularResponseListener() {
        @Override
        public void didFetch(RandomSpoonacularResponse response, String message) {
            dialog.dismiss();
            sliderView = rootView.findViewById(R.id.image_slider);
            imageSliderAdapter = new RandomSliderAdapter(mContext,response.recipes,recipeClickListener,featureListener);
            sliderView.setSliderAdapter(imageSliderAdapter);


            db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference().child("recipes");
            for (Recipe recipe : response.recipes) {
                String pushId = databaseReference.push().getKey();
                databaseReference.child(pushId).setValue(recipe);
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(mContext,message, Toast.LENGTH_SHORT);
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void OnRecipeClicked(String id) {
            Intent intent = new Intent(getActivity(), RecipesDetails.class)
                    .putExtra("id",id);
            startActivity(intent);
            //Toast.makeText(getContext(),id,Toast.LENGTH_SHORT).show();
        }
    };

    private void getFirebaseRecipes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes");
        Query query = databaseReference.orderByChild("featured").equalTo(true).limitToFirst(4);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                featuredRecipeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    featuredRecipeList.add(recipe);
                }
                sliderView = rootView.findViewById(R.id.image_slider);
                imageSliderAdapter = new RandomSliderAdapter(mContext, featuredRecipeList, recipeClickListener, featureListener);
                sliderView.setSliderAdapter(imageSliderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}