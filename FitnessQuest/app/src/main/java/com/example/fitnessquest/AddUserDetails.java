package com.example.fitnessquest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddUserDetails extends AppCompatActivity {

    MaterialAutoCompleteTextView inputAge;
    TextInputLayout txtHeight, txtWeight;
    TextInputEditText inputHeight, inputWeight;
    MaterialButton btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setVariables();
        setAgeArray();

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUserGoal.class);
            startActivity(intent);
        });
    }
    void setVariables() {
        inputAge = findViewById(R.id.inputAge);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        btnNext = findViewById(R.id.btnContinueToGoals);
    }

    void setAgeArray() {
        // Get the age options from resources
        String[] ageOptions = getResources().getStringArray(R.array.age_options);

        // Set up the ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageOptions);
        inputAge.setAdapter(adapter);
    }
}