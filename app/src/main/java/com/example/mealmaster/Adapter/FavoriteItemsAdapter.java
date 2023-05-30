package com.example.mealmaster.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.R;
import com.example.mealmaster.model.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoriteItemsAdapter extends RecyclerView.Adapter<FavoriteItemsAdapter.ViewHolder>{

    private ArrayList<Recipe> favoriteRecipes;
    android.content.Context context;




    public FavoriteItemsAdapter(android.content.Context context,ArrayList<Recipe> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    @NonNull
    @Override
    public FavoriteItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemsAdapter.ViewHolder holder, int position) {

        Recipe recipeList = favoriteRecipes.get(position);

        holder.textView_favoris.setText(recipeList.getTitle());
        holder.textView_favoris.setSelected(true);
        Picasso.get().load(recipeList.getImage()).into(holder.favoris_img);

    }

    @Override
    public int getItemCount() {
        return favoriteRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView favoris_list;
        ImageView favoris_img;
        TextView textView_favoris;
        Button fvrtBt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favoris_list = itemView.findViewById(R.id.favorite_list);
            favoris_img = itemView.findViewById(R.id.favorite_title);
            textView_favoris = itemView.findViewById(R.id.image_favorite);
            fvrtBt = itemView.findViewById(R.id.btn_like);
        }
    }
}
