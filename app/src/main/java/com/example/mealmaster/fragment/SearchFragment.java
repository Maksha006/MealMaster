package com.example.mealmaster.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

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
    private static final String API_KEY = "af3b71ca41664ff586770e97ce55e795";
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
                new SpoonacularRecipeRequest(selectedTag).execute(searchIngredients);
            }
        });
        return rootView;
    }

    public class SpoonacularRecipeRequest extends AsyncTask<String, Void, List<Recipe>> {

        private String selectedTag;

        public SpoonacularRecipeRequest(String selectedTag) {
            this.selectedTag = selectedTag;
        }

        @Override
        protected List<Recipe> doInBackground(String... searchIngredients) {
            List<Recipe> recipes = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spoonacular.com/recipes/complexSearch").newBuilder();
            urlBuilder.addQueryParameter("query", searchIngredients[0]);
            urlBuilder.addQueryParameter("type", selectedTag); // Ajout du paramètre "type"
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
                // Démarrer SearchResultsActivity
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra("recipes", (Serializable) recipes);
                startActivity(intent);
            }else {
                // Afficher un message disant qu'aucune recette n'a été trouvée
                Toast.makeText(getActivity(), "Aucune recette trouvée", Toast.LENGTH_LONG).show();
            }
        }
    }
}