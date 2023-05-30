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
import com.example.mealmaster.model.InstructionsResponse;

import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructuionViewHolder> {

    Context context;
    List<InstructionsResponse> list;

    public InstructionsAdapter(Context context, List<InstructionsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructuionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructuionViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_recipes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructuionViewHolder holder, int position) {

        holder.textView_instruction_name.setText(list.get(position).name);
        holder.recycler_instructions_steps.setHasFixedSize(true);
        holder.recycler_instructions_steps.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        InstructionsStepsAdapter stepsAdapter = new InstructionsStepsAdapter(context,list.get(position).steps);
        holder.recycler_instructions_steps.setAdapter(stepsAdapter);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructuionViewHolder extends RecyclerView.ViewHolder{

    TextView textView_instruction_name;
    RecyclerView recycler_instructions_steps;

    public InstructuionViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instruction_name = itemView.findViewById(R.id.tv_instructions_name);
        recycler_instructions_steps = itemView.findViewById(R.id.recycler_instructions_steps);
    }
}
