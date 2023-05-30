package com.example.mealmaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmaster.R;
import com.example.mealmaster.model.Step;

import java.util.List;

public class InstructionsStepsAdapter extends RecyclerView.Adapter<InstructionStepsViewHolder> {

    Context context;
    List<Step> list;

    public InstructionsStepsAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_recipes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepsViewHolder holder, int position) {

        holder.tv_instructions_steps_number.setText(String.valueOf(list.get(position).number));
        holder.tv_instructions_steps_title.setText(list.get(position).step);

        holder.recycler_instructions_ingredients.setHasFixedSize(true);
        holder.recycler_instructions_ingredients.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        InstructionsIngredientsAdapter instructionsIngredientsAdapter = new InstructionsIngredientsAdapter(list.get(position).ingredients,context);
        holder.recycler_instructions_ingredients.setAdapter(instructionsIngredientsAdapter);

        holder.recycler_instructions_equipements.setHasFixedSize(true);
        holder.recycler_instructions_equipements.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        InstructionsEquipementsAdapter instructionsEquipementsAdapter = new InstructionsEquipementsAdapter(context,list.get(position).equipment);
        holder.recycler_instructions_equipements.setAdapter(instructionsEquipementsAdapter);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionStepsViewHolder extends RecyclerView.ViewHolder{

    TextView tv_instructions_steps_title,tv_instructions_steps_number;
    RecyclerView recycler_instructions_equipements,recycler_instructions_ingredients;

    public InstructionStepsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_instructions_steps_title = itemView.findViewById(R.id.tv_instructions_steps_title);
        tv_instructions_steps_number = itemView.findViewById(R.id.tv_instructions_steps_number);
        recycler_instructions_ingredients = itemView.findViewById(R.id.recycler_instructions_ingredients);
        recycler_instructions_equipements = itemView.findViewById(R.id.recycler_instructions_equipements);
    }
}
