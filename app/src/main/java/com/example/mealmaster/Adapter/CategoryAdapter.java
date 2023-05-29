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
import com.example.mealmaster.SpoonacularManager;
import com.example.mealmaster.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    int catArr[];

    private List<Recipe> list;

    String tagArr[];

    CategoryClickListener listener;

    public CategoryAdapter(int[] catArr,String[] tagArr,CategoryClickListener listener) {
        this.catArr = catArr;
        this.tagArr = tagArr;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_recipe_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Category_image.setImageResource(catArr[position]);
        holder.Category_name.setText(tagArr[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategoryClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return catArr.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Category_name;
        ImageView Category_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Category_name = itemView.findViewById(R.id.Category_name);
            Category_image = itemView.findViewById(R.id.Category_image);
        }
    }
}
