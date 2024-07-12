package com.example.fitnessquest;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessquest.adapters.ExerciseListRvAdapter;
import com.example.fitnessquest.client.ApiClient;
import com.example.fitnessquest.client.ApiInterface;
import com.google.android.material.appbar.MaterialToolbar;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExcerciseListScreen extends AppCompatActivity {
    RecyclerView r_view;
    MaterialToolbar toolbar;
    TextView tv_heading;
    ExerciseListRvAdapter byPartsRServiceListAdapter;
    List<String> data=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_by_parts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String slug = getIntent().getStringExtra("SLUG");

        toolbar=findViewById(R.id.topAppBar_by_parts);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tv_heading=findViewById(R.id.heading);
        String heading = getIntent().getStringExtra("HEADING");
        tv_heading.setText(heading);

        r_view=findViewById(R.id.by_parts_r_view);
        byPartsRServiceListAdapter=new ExerciseListRvAdapter(ExcerciseListScreen.this,data,slug);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        r_view.setLayoutManager(mLayoutManager);
        r_view.setAdapter(byPartsRServiceListAdapter);

        String type = getIntent().getStringExtra("TYPE");



        populateService(type);
    }

    public void populateService(String type){



        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);



        Call<List<String>> call = apiInterface.getBodyParts(type);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                System.out.println("Called  Successfully");

                if (!response.isSuccessful()) {
                    System.out.println("Called Not Successfully");
                    return;
                }
                List<String> bodyParts = response.body();
                System.out.println(bodyParts);
                if (bodyParts != null) {
                    data.addAll(bodyParts);
                    byPartsRServiceListAdapter.notifyDataSetChanged();
                } else {
                    System.out.println("Response body is null");
                }
            }


            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Handle failure
            }
        });

    }
}

