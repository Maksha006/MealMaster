package com.example.mealmaster.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.FirebaseManager;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.R;
import com.example.mealmaster.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchRecipeAdapter extends RecyclerView.Adapter<SearchRecipeAdapter.ViewHolder> {

    Context context;

    List<Recipe> recipes;

    Recipe recipe;

    RecipeClickListener listener;

    public interface RecipeFeatureClickListener {
        void onRecipeFeatureClick(String recipeId);
    }

    private RecipeFeatureClickListener featureListener;

    public SearchRecipeAdapter(Context context, List<Recipe> recipes, RecipeClickListener listener, RecipeFeatureClickListener featureListener) {
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;
        this.featureListener = featureListener;
    }

    @NonNull
    @Override
    public SearchRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecipeAdapter.ViewHolder holder, int position) {

        Recipe recipe = recipes.get(position);

        holder.DishName_title.setText(recipe.getTitle());
        holder.DishName_title.setSelected(true);
        String imageUrl = recipe.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.image_food);
        } else {
            Picasso.get().load(recipe.getImage()).into(holder.image_food); // mettre une image de placeholder si l'url de l'image est vide ou nulle
        }

        holder.search_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipeId = String.valueOf(recipes.get(holder.getAdapterPosition()).getId());
                Log.d("RecipeClickListener", "Clicked recipe ID: " + recipeId);
                listener.OnRecipeClicked(recipeId);
            }
        });

        holder.btn_vedette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe recipe = recipes.get(holder.getAdapterPosition());
                FirebaseManager firebaseManager = new FirebaseManager();
                firebaseManager.setRecipe(recipe);

                recipe.setFavorite(!recipe.isFavorite());
                firebaseManager.saveRecipe(recipe);

                if (recipe.isFavorite()) {
                    holder.btn_vedette.setBackgroundResource(R.drawable.ic_favorite);
                } else {
                    holder.btn_vedette.setBackgroundResource(R.drawable.ic_favoritewhite);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView search_list_container;

       // ImageView category_list_contenair;
        TextView DishName_title;
        ImageView image_food;

        Button btn_vedette;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            search_list_container = itemView.findViewById(R.id.search_list_container);
            //category_list_contenair = itemView.findViewById(R.id.category_list_container);
            DishName_title = itemView.findViewById(R.id.Dish_title);
            image_food = itemView.findViewById(R.id.picture_food);
            btn_vedette = itemView.findViewById(R.id.btn_vedette);
        }
    }
}
