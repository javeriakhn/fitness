package com.example.fitnessquest;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessquest.adapters.BodyPartsExercisesAdapter;
import com.example.fitnessquest.client.ApiClient;
import com.example.fitnessquest.client.ApiInterfaceAllExcercises;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllExercisesByBody extends AppCompatActivity {
    TextView headTxt;
    RecyclerView r_view;
    MaterialToolbar toolbar;
    BodyPartsExercisesAdapter bodyPartsExercisesAdapter;
    List<ExercisesBodyParts> data= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_exercises_by_body);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        headTxt=findViewById(R.id.heading_body_screen);
        r_view=findViewById(R.id.r_view_body_exercises);
        toolbar=findViewById(R.id.topAppBar_ByBody);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        bodyPartsExercisesAdapter=new BodyPartsExercisesAdapter(AllExercisesByBody.this,data);
        r_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        r_view.setAdapter(bodyPartsExercisesAdapter);

        String bodyPartName = getIntent().getStringExtra("BODY_PART_NAME");
        String slug = getIntent().getStringExtra("SLUG");

        headTxt.setText(bodyPartName+ getString(R.string.exercises));

        populateService(bodyPartName,slug);

    }

    public void populateService(String name,String slug) {
        System.out.println("I am Called for " + name);
        ApiInterfaceAllExcercises apiInterface= ApiClient.getClient().create(ApiInterfaceAllExcercises.class);
        Call<List<ExercisesBodyParts>> call = apiInterface.getExercisesForBodyPart(slug,name); // Use lowercase "back" as per the endpoint
        call.enqueue(new Callback<List<ExercisesBodyParts>>() {
            @Override
            public void onResponse(Call<List<ExercisesBodyParts>> call, Response<List<ExercisesBodyParts>> response) {
                System.out.println("Exercise Called Successfully");

                if (!response.isSuccessful()) {
                    System.out.println("Exercise Called Unsuccessful");
                    return;
                }
                List<ExercisesBodyParts> exercises = response.body();
                if (exercises != null) {
                    data.addAll(exercises);
                    bodyPartsExercisesAdapter.notifyDataSetChanged();
                } else {
                    System.out.println("Exercise body is null");
                }
            }

            @Override
            public void onFailure(Call<List<ExercisesBodyParts>> call, Throwable t) {
                // Handle failure
            }
        });
    }

}