package com.example.mealmaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.R;
import com.example.mealmaster.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryRecipeAdapter extends RecyclerView.Adapter<CategoryRecipeAdapter.ViewHolder> {

    Context context;
    private List<Recipe> recipes;

    public CategoryRecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public CategoryRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_of_food, parent, false);
        return new CategoryRecipeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecipeAdapter.ViewHolder holder, int position) {
        holder.DishName_title.setText(recipes.get(position).getTitle());
        holder.DishName_title.setSelected(true);
        String imageUrl = recipes.get(position).getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.image_food);
        } else {
            Picasso.get().load(recipes.get(position).getImage()).into(holder.image_food); // mettre une image de placeholder si l'url de l'image est vide ou nulle
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView category_list_container;
        TextView DishName_title;
        ImageView image_food;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_list_container = itemView.findViewById(R.id.category_list_container);
            DishName_title = itemView.findViewById(R.id.DishName_category);
            image_food = itemView.findViewById(R.id.image_category_food);
        }
    }
}
