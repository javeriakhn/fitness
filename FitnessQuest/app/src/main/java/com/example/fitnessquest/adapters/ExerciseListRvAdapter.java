package com.example.fitnessquest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessquest.AllExercisesByBody;
import com.example.fitnessquest.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;


public class ExerciseListRvAdapter extends RecyclerView.Adapter<ExerciseListRvAdapter.ViewHolder> {

    Context context;
    List<String> data;

    String slug;
    public ExerciseListRvAdapter(Context context, List<String> byPartsData,String slug) {
        this.context=context;
        this.data =byPartsData;
        this.slug=slug;
    }

    @NonNull
    @Override
    public ExerciseListRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rv_by_parts_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListRvAdapter.ViewHolder holder, int position) {
        String bodyPart = data.get(position);
        holder.bind(bodyPart);

        holder.start_exercise.setOnClickListener( v -> {
            if (position != RecyclerView.NO_POSITION) {
                String selectedBodyPart = data.get(position);
                Intent intent = new Intent(context, AllExercisesByBody.class);
                System.out.println("Shoiab"+ selectedBodyPart);
                intent.putExtra("BODY_PART_NAME", selectedBodyPart);
                intent.putExtra("SLUG", slug);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView parts_name;
    private MaterialButton start_exercise;
        public ViewHolder(View itemView) {
            super(itemView);
            parts_name=itemView.findViewById(R.id.rv_items);
            start_exercise=itemView.findViewById(R.id.startExcercise);
            itemView.setOnClickListener(this);

        }
        public  void bind(String name){
            parts_name.setText(name);
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String selectedBodyPart = data.get(position);
                Intent intent = new Intent(context, AllExercisesByBody.class);
                System.out.println("Shoiab"+ selectedBodyPart);
                intent.putExtra("BODY_PART_NAME", selectedBodyPart);
                intent.putExtra("SLUG", slug);
                context.startActivity(intent);
            }
        }
    }
}
