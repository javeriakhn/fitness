package com.example.fitnessquest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.example.fitnessquest.ExerciseDetail;
import com.example.fitnessquest.ExercisesBodyParts;
import com.example.fitnessquest.R;
import com.google.android.material.button.MaterialButton;

public class BodyPartsExercisesAdapter extends RecyclerView.Adapter<BodyPartsExercisesAdapter.ViewHolder>{

    private Context context;
    private List<ExercisesBodyParts> exercises;

    public BodyPartsExercisesAdapter(Context context, List<ExercisesBodyParts> exercises) {
        this.context = context;
        this.exercises = exercises;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_exercises_by_body_parts, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExercisesBodyParts exercise = exercises.get(position);
        holder.bind(exercise);

    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView, bodyPartTextView;
        MaterialButton equipmentBtn;
        private ImageView imgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            imgView=itemView.findViewById(R.id.imageViewGif);
            bodyPartTextView = itemView.findViewById(R.id.textViewBodyPart);
            equipmentBtn = itemView.findViewById(R.id.btnEquipmentType);
            itemView.setOnClickListener(this);
        }

        public void bind(ExercisesBodyParts exercise) {
            nameTextView.setText(exercise.getName());
            bodyPartTextView.setText("Affects "+exercise.getBodyPart());
            equipmentBtn.setText(exercise.getEquipment());

            // Load GIF image using Glide
            Glide.with(context)
                    .load(exercise.getGifUrl())
                    .fitCenter()
                    .into(imgView);


        }

//        int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION) {
//            String selectedBodyPart = byPartsData.get(position);
//            System.out.println("Clicked"+ selectedBodyPart);
//            Intent intent = new Intent(context, AllExercisesByBody.class);
//            intent.putExtra("BODY_PART_NAME", selectedBodyPart);
//            context.startActivity(intent);
//        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ExercisesBodyParts clickedExercise = exercises.get(position);
                System.out.println(clickedExercise.getInstructions());
                Intent intent = new Intent(context, ExerciseDetail.class);
                intent.putExtra("EXERCISE_NAME", clickedExercise.getName());
                intent.putExtra("BODY_PART", clickedExercise.getBodyPart());
                intent.putExtra("EQUIPMENT", clickedExercise.getEquipment());
                intent.putExtra("GIF_IMAGE", clickedExercise.getGifUrl());
                intent.putExtra("TARGET", clickedExercise.getTarget());
                intent.putExtra("EX_ID", clickedExercise.getId());

                intent.putStringArrayListExtra("INSTRUCTIONS", clickedExercise.getInstructions());
                intent.putStringArrayListExtra("SECONDARY_MUSCLE", clickedExercise.getSecondaryMuscles());

                System.out.println("ADAPTIER");
                System.out.println(clickedExercise.getSecondaryMuscles());


                context.startActivity(intent);

            }
        }
    }
}
