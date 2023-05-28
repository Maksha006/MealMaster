package com.example.mealmaster.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment /*implements SharedPreferences.OnSharedPreferenceChangeListener*/ {

    private Context mContext;
    private View rootView;

    Button btVedette;

    /////
    FloatingActionButton fbFav;
    FirebaseDatabase db;

    FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;
    Boolean fvrtChecked = false;
    /////

    /*private String getString;
    SeekBar seekBar1,seekBar2;
    TextView Option1,Option2;
    TextView percent1,percent2;
    double count1 = 1 ,count2 = 1;
    boolean flag1 = true, flag2 = true;*/

    private final String API_KEY = "af3b71ca41664ff586770e97ce55e795";

    private static final int SEARCH_NUMBER = 2;


    // graphisme (bouton,titre,images) pour le tournoi //
    ImageView im_recipe1;
    ImageView im_recipe2;

    Button bt_vote1;
    Button bt_vote2;
    ///////////////////////////////////

    private SliderView sliderView;
    private RandomSliderAdapter imageSliderAdapter;

    ProgressDialog dialog;
    SpoonacularManager manager;

    private static final String SHARED_PREFERENCES_NAME = "MySharedPreferences";

  /*  private static final String SEEK_BAR_1_KEY = "seekBar1";
    private static final String SEEK_BAR_2_KEY = "seekBar2";*/
    private List<Recipe> recipeList = new ArrayList<>();

    private RandomSliderAdapter.RecipeFeatureClickListener featureListener;

    private List<Recipe> featuredRecipeList = new ArrayList<>();
    private List<Recipe> RecipeList = new ArrayList<>();

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

        // image et bouton pour le tournoi //////////////////
        im_recipe1 = rootView.findViewById(R.id.recipeImage1);
        im_recipe2 = rootView.findViewById(R.id.recipeImage2);
        bt_vote1 = rootView.findViewById(R.id.voteButton1);
        bt_vote2 = rootView.findViewById(R.id.voteButton2);
        /////////////////////////////////////////////////////

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(mContext);
        dialog.setTitle("Loading...");

        bt_vote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForRecipe(recipeList.get(0).getTitle());
            }
        });

        bt_vote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForRecipe(recipeList.get(1).getTitle());
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

        new TournamentRecipeRequest().execute();

        return rootView;
    }

    // récupération des recettes pour le tournoi //
    public class TournamentRecipeRequest extends AsyncTask<String, Void, List<Recipe>>{
        @Override
        protected List<Recipe> doInBackground(String... tournamentRecipes) {
            List<Recipe> VoteRecipes = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spoonacular.com/recipes/complexSearch").newBuilder();
            urlBuilder.addQueryParameter("query", "pizza");
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
                    Recipe recipe = new Recipe(name, imageURL, 0); // Init votes to 0

                    String recipeKey = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                    // Insert the recipe to Firebase
                    DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("recipes");
                   // String recipeId = recipesRef.push().getKey(); // Generates a unique ID for the new recipe

                    // Saves the new recipe in the database
                    recipesRef.child(recipeKey).setValue(recipe);

                    VoteRecipes.add(recipe);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return VoteRecipes;
        }
    @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            if (recipes != null && recipes.size() >= 2){
                recipeList.clear();
                recipeList.addAll(recipes);

                String nameRecipe1 = recipeList.get(0).getTitle();
                String nameRecipe2 = recipeList.get(1).getTitle();

                String imageUrl1 = recipeList.get(0).getImage(); // obtenir l'URL de la première image
                String imageUrl2 = recipeList.get(1).getImage(); // obtenir l'URL de la deuxième image

                // Utilisez Picasso pour charger les images dans vos ImageView
                Picasso.get().load(imageUrl1).into(im_recipe1);
                Picasso.get().load(imageUrl2).into(im_recipe2);
            }
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

  /*  private void calculatePercent() {

        double total = count1 + count2;
        double recipePercent1 = (count1/total)*100;
        double recipePercent2 = (count2/total)*100;

        percent1.setText(String.format("%.2f",recipePercent1));
        seekBar1.setProgress((int) recipePercent1);

        percent2.setText(String.format("%.2f",recipePercent2));
        seekBar2.setProgress((int) recipePercent2);
    }*/


  /*  private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (seekBar.getId() == R.id.seek_bar1) {
                editor.putInt("seekBar1Value", progress);
            } else if (seekBar.getId() == R.id.seek_bar2) {
                editor.putInt("seekBar2Value", progress);
            }
            editor.apply();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("METAPX","onDestroy is CALLED");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("seekBar1Value")) {
            SeekBar seekBar1 = rootView.findViewById(R.id.seek_bar1);
            seekBar1.setProgress(sharedPreferences.getInt(s, 0));
        } else if (s.equals("seekBar2Value")) {
            SeekBar seekBar2 = rootView.findViewById(R.id.seek_bar2);
            seekBar2.setProgress(sharedPreferences.getInt(s, 0));
        }
    }*/

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