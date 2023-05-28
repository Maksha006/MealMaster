package com.example.mealmaster.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

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

    private String getString;
    SeekBar seekBar1,seekBar2;
    TextView Option1,Option2;
    TextView percent1,percent2;
    double count1 = 1 ,count2 = 1;
    boolean flag1 = true, flag2 = true;

    private final String API_KEY = "af3b71ca41664ff586770e97ce55e795";

    private SliderView sliderView;
    private RandomSliderAdapter imageSliderAdapter;

    ProgressDialog dialog;
    SpoonacularManager manager;

    private static final String SHARED_PREFERENCES_NAME = "MySharedPreferences";

    private static final String SEEK_BAR_1_KEY = "seekBar1";
    private static final String SEEK_BAR_2_KEY = "seekBar2";
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

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(mContext);
        dialog.setTitle("Loading...");

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

       //Assign variable du seekbar
        seekBar1 = rootView.findViewById(R.id.seek_bar1);
        seekBar2 = rootView.findViewById(R.id.seek_bar2);

        //Assign variable des Options
        Option1 = rootView.findViewById(R.id.option1);
        Option2 = rootView.findViewById(R.id.option2);

        //Assign variable des pourcentages
        percent1 = rootView.findViewById(R.id.percent1);
        percent2 = rootView.findViewById(R.id.percent2);

        // obtenir une instance of SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        seekBar1.setProgress(sharedPreferences.getInt("seekBar1Value", 0));
        seekBar2.setProgress(sharedPreferences.getInt("seekBar2Value", 0));

        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener);



        seekBar1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        Option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check the Options
                if (flag1){
                    count1++;
                    count2 = 1;
                    flag1 = false;
                    flag2 = true;
                    // calculate percentage
                    calculatePercent();
                }
            }
        });

        seekBar2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        Option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check the Options
                if (flag1){
                    count1 = 1;
                    count2 ++;
                    flag1 = true;
                    flag2 = false;
                    // calculate percentage
                    calculatePercent();
                }
            }
        });
        return rootView;
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

    private void calculatePercent() {

        double total = count1 + count2;
        double recipePercent1 = (count1/total)*100;
        double recipePercent2 = (count2/total)*100;

        percent1.setText(String.format("%.2f",recipePercent1));
        seekBar1.setProgress((int) recipePercent1);

        percent2.setText(String.format("%.2f",recipePercent2));
        seekBar2.setProgress((int) recipePercent2);
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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
    }

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