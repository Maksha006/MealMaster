package com.example.mealmaster.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.Listeners.CategoryClickListener;
import com.example.mealmaster.R;
import com.example.mealmaster.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MapsRecipeAdapter extends RecyclerView.Adapter<MapsRecipeAdapter.ViewHolder>{

    private List<Recipe> list;

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
        Picasso.get().load(list.get(position).getImage()).into(holder.maps_dish_image);
        holder.maps_dish_name.setText(list.get(position).getTitle());
        holder.maps_dish_name.setSelected(true);
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
