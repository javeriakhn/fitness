package com.example.fitnessquest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    MaterialToolbar topAppBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    CardView cardView;
    CardView cardView_by_equip;
    CardView cardView_by_target;


    TextView caloryCountTextView;
    TextView bmiTextView;

    CircleImageView profileIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setSupportActionBar(topAppBar);
        setVariables();

        caloryCountTextView = findViewById(R.id.caloryCount);
        bmiTextView = findViewById(R.id.bmiCount);

        // Fetch and display the current user's calorie count and BMI
        fetchUserData();

        // Setting up the hamburger icon to open the drawer
        topAppBar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView); // Close drawer if open
            } else {
                drawerLayout.openDrawer(navigationView); // Open drawer
            }
        });

        // Mark the current activity's menu item as selected
        navigationView.setCheckedItem(R.id.home);

        cardView = findViewById(R.id.categoryCard);
        cardView_by_equip = findViewById(R.id.categoryCard2);
        cardView_by_target = findViewById(R.id.categoryCard3);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                drawerLayout.closeDrawer(navigationView);
                // Already in home, do nothing
            } else if (itemId == R.id.goals) {
                Intent intent = new Intent(this, GoalsTracking.class);
                startActivity(intent);
            } else if (itemId == R.id.leaderboard) {
                Intent intent = new Intent(this, Leaderboard.class);
                startActivity(intent);
            } else if (itemId == R.id.about) {
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(Main.this, ExcerciseListScreen.class);
            intent.putExtra("TYPE", "bodyPartList");
            intent.putExtra("HEADING", "Exercise by Body Parts");
            intent.putExtra("SLUG", "bodyPart");
            startActivity(intent);
        });

        cardView_by_equip.setOnClickListener(v -> {
            Intent intent = new Intent(Main.this, ExcerciseListScreen.class);
            intent.putExtra("TYPE", "equipmentList");
            intent.putExtra("HEADING", "Exercise by Equipments");
            intent.putExtra("SLUG", "equipment");
            startActivity(intent);
        });

        cardView_by_target.setOnClickListener(v -> {
            Intent intent = new Intent(Main.this, ExcerciseListScreen.class);
            intent.putExtra("TYPE", "targetList");
            intent.putExtra("HEADING", "Exercise by Target");
            intent.putExtra("SLUG", "target");
            startActivity(intent);
        });

        profileIcon.setOnClickListener( v -> {
                Intent intent = new Intent(Main.this, Profile.class);
                startActivity(intent);
        });

    }

    void setVariables() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        topAppBar = findViewById(R.id.topAppBar);
        navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(topAppBar);
        profileIcon = findViewById(R.id.profileIcon);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Mark the current activity's menu item as selected
        navigationView.setCheckedItem(R.id.home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch and display the current user's calorie count and BMI every time the activity resumes
        fetchUserData();
        // Mark the current activity's menu item as selected
        navigationView.setCheckedItem(R.id.home);
    }

    private void fetchUserData() {
        if (currentUser != null) {
            String uID = currentUser.getUid();
            DocumentReference userDoc = db.collection("users").document(uID);

            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Long currentCalories = document.getLong("CaloriesBurnt");
                            if (currentCalories != null) {
                                caloryCountTextView.setText(String.valueOf(currentCalories));
                            } else {
                                caloryCountTextView.setText("0");
                            }

                            Long height = document.getLong("Height");
                            Long weight = document.getLong("Weight");
                            if (height != null && weight != null && height > 0 && weight > 0) {
                                double heightInMeters = height / 100.0;
                                double bmi = weight / (heightInMeters * heightInMeters);
                                bmiTextView.setText(String.format("%.2f", bmi));
                            } else {
                                bmiTextView.setText("N/A");
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}
