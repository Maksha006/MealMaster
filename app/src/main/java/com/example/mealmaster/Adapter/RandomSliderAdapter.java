package com.example.mealmaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.mealmaster.FirebaseManager;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.model.Recipe;
import com.example.mealmaster.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RandomSliderAdapter extends SliderViewAdapter<SliderAdapterVH> {

    Context context;
    List<Recipe> list;
    RecipeClickListener listener;
    Recipe recipe;

    public interface RecipeFeatureClickListener {
        void onRecipeFeatureClick(String recipeId);
    }

    // Dans votre adaptateur
    private RecipeFeatureClickListener featureListener;

    public RandomSliderAdapter(Context context, List<Recipe> list, RecipeClickListener listener, RecipeFeatureClickListener featureListener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.featureListener = featureListener;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);

        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

        Recipe recipe = list.get(position);

        viewHolder.DishName_title.setText(recipe.getTitle());
        viewHolder.DishName_title.setSelected(true);
        Picasso.get().load(recipe.getImage()).into(viewHolder.image_food);

        viewHolder.random_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseManager firebaseManager = new FirebaseManager();
                firebaseManager.setRecipe(recipe);

                // Mettre à jour l'attribut isFavorite de la recette
                recipe.setFavorite(!recipe.isFavorite());
                firebaseManager.saveRecipe(recipe);

                // Mettre à jour l'icône du cœur dans la vue
                if (recipe.isFavorite()) {
                    viewHolder.btn_like.setImageResource(R.drawable.ic_favorite);
                } else {
                    viewHolder.btn_like.setImageResource(R.drawable.ic_favoritewhite);
                }

                // Mettre à jour le listener avec l'ID de la recette
                String recipeId = String.valueOf(recipe.getId());
                listener.OnRecipeClicked(recipeId);
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }
}

class SliderAdapterVH extends SliderViewAdapter.ViewHolder{
    CardView random_list_container;
    TextView DishName_title;
    ImageView image_food;
    ImageView btn_like;

    public SliderAdapterVH(View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        DishName_title = itemView.findViewById(R.id.DishName_title);
        image_food = itemView.findViewById(R.id.image_food);
        btn_like = itemView.findViewById(R.id.btn_like);
    }
}