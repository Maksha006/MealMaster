package com.example.mealmaster.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.Listeners.CategoryClickListener;
import com.example.mealmaster.Listeners.RecipeClickListener;
import com.example.mealmaster.R;
import com.example.mealmaster.RecipesDetails;
import com.example.mealmaster.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MapsRecipeAdapter extends RecyclerView.Adapter<MapsRecipeAdapter.ViewHolder>{

    private List<Recipe> list;
    RecipeClickListener listener;

    public MapsRecipeAdapter(List<Recipe> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MapsRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_maps_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsRecipeAdapter.ViewHolder holder, int position) {

        Recipe recipe = list.get(position);


        Picasso.get().load(list.get(position).getImage()).into(holder.maps_dish_image);
        holder.maps_dish_name.setText(recipe.getTitle());
        holder.maps_dish_name.setSelected(true);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, RecipesDetails.class);

            // Passez l'ID de la recette à votre activité de détails.
            // Assurez-vous que votre classe Recipe a une méthode getId().
            // Remarque : cela suppose que l'ID de la recette est un String. Si c'est un int ou un autre type de données, veuillez modifier en conséquence.
            intent.putExtra("Mapsid", recipe.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView maps_dish_name;
        ImageView maps_dish_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            maps_dish_name = itemView.findViewById(R.id.maps_dish_name);
            maps_dish_image = itemView.findViewById(R.id.maps_dish_image);
        }
    }
}
