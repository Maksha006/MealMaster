package com.example.mealmaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.R;
import com.example.mealmaster.model.Equipment;
import com.example.mealmaster.model.Ingredient;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructionsEquipementsAdapter extends RecyclerView.Adapter<InstructionsEquipementsViewHolder>{

    Context context;
    List<Equipment>ingredients;


    public InstructionsEquipementsAdapter(Context context,List<Equipment> ingredients) {
        this.context = context;
        this.ingredients = ingredients;

    }

    @NonNull
    @Override
    public InstructionsEquipementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsEquipementsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsEquipementsViewHolder holder, int position) {
        holder.tv_instruction_step_items.setText(ingredients.get(position).name);
        holder.tv_instruction_step_items.setSelected(true);
        Picasso.get().load("https://spoonacular.com/cdn/equipment_100x100/"+ingredients.get(position).image).into(holder.im_instruction_step_items);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
class InstructionsEquipementsViewHolder extends RecyclerView.ViewHolder{

    ImageView im_instruction_step_items;
    TextView tv_instruction_step_items;

    public InstructionsEquipementsViewHolder(@NonNull View itemView) {
        super(itemView);
        im_instruction_step_items = itemView.findViewById(R.id.im_instruction_step_items);
        tv_instruction_step_items = itemView.findViewById(R.id.tv_instruction_step_items);
    }
}
