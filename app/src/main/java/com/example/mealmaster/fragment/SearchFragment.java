package com.example.mealmaster.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mealmaster.Adapter.CategoryAdapter;
import com.example.mealmaster.ListOfRecipes;
import com.example.mealmaster.Listeners.CategoryClickListener;
import com.example.mealmaster.R;
import com.example.mealmaster.RecipesDetails;
import com.example.mealmaster.SearchResultsActivity;
import com.example.mealmaster.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.XMLFormatter;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {

    private View rootView;
    private static final String API_KEY = "zRMMlpwqL9NvB6uMn1m8scsccUxIesRv";
    private static final int SEARCH_NUMBER = 25;
    private EditText ingredientTxt;
    private Button submitBtn;
    ProgressDialog dialog;
    private RecyclerView category_recyclerView;

    RecyclerView.LayoutManager layoutManager;
    CategoryAdapter categoryAdapter;

    String tagArr[];

    String selectedTag;

    int [] arr ={R.drawable.main_course,R.drawable.side_dish,R.drawable.breakfast,R.drawable.salad,
            R.drawable.soup,R.drawable.dessert_img,R.drawable.fingerfood,R.drawable.vegetarian,
            R.drawable.gluten_free};

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        tagArr = getResources().getStringArray(R.array.tags);

        // CATEGORIES DE RECETTES //
        category_recyclerView = rootView.findViewById(R.id.category_recyclerView);
        layoutManager = new GridLayoutManager(getContext(),2);
        category_recyclerView.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(arr,tagArr,new CategoryClickListener() {
            @Override
            public void onCategoryClick(int position) {
                selectedTag = tagArr[position]; // Stocker le tag sélectionné

                Intent intent = new Intent(getActivity(),ListOfRecipes.class);
                intent.putExtra("selectedTag", selectedTag);
                startActivity(intent);
            }
        });

        category_recyclerView.setAdapter(categoryAdapter);
        category_recyclerView.setHasFixedSize(true);

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");

        ingredientTxt = rootView.findViewById(R.id.ingredientsTxt);
        submitBtn = rootView.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchIngredients = ingredientTxt.getText().toString();
                new SpoonacularRecipeRequest(getContext(), selectedTag).execute(searchIngredients);
            }
        });
        return rootView;
    }

    public class SpoonacularRecipeRequest extends AsyncTask<String, Void, List<Recipe>> {

        private String selectedTag;
        private Context context;

        public SpoonacularRecipeRequest(Context context,String selectedTag) {
            this.context = context;
            this.selectedTag = selectedTag;
        }

        @Override
        protected List<Recipe> doInBackground(String... searchIngredients) {
            List<Recipe> recipes = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.apilayer.com/spoonacular/recipes/complexSearch").newBuilder();
            urlBuilder.addQueryParameter("query", searchIngredients[0]);
            urlBuilder.addQueryParameter("type", selectedTag); // Ajout du paramètre "type"
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
            if (recipes != null && recipes.size() > 0) {
                // Initialisez les données des recettes
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    initializeRecipeData(recipes, user.getUid());
                }

                // Démarrer SearchResultsActivity
                Intent intent = new Intent(context, SearchResultsActivity.class);
                intent.putExtra("recipes", (Serializable) recipes);
                context.startActivity(intent);
            } else {
                // Afficher un message disant qu'aucune recette n'a été trouvée
                Toast.makeText(context, "Aucune recette trouvée", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeRecipeData(List<Recipe> recipes, String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");
        for (Recipe recipe : recipes) {
            String recipeId = String.valueOf(recipe.getId());
            databaseReference.child(recipeId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        recipe.setFavorite(snapshot.getValue(Boolean.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

}