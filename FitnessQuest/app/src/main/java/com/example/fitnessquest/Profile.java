package com.example.fitnessquest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    CircleImageView profileImage;
    ImageView changeProfileImage;
    TextView name, email, gender, age, completedWorkouts, weight, height, changePassword, caloriesBurned;
    MaterialButton logout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    private static final String TAG = "UserProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setVariables();
        getUserData();
        getFireStoreData();

        changeProfileImage.setOnClickListener(v -> {
            String emailAddress = user.getEmail();
            showResetPasswordDialog(this, emailAddress);
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            //finish();
        });


    }
    void setVariables() {
        profileImage = findViewById(R.id.profileImage);
        name = findViewById(R.id.txtUserFullname);
        email = findViewById(R.id.txtUserEmail);
        changeProfileImage = findViewById(R.id.changePfp);
        gender = findViewById(R.id.txtGender);
        age = findViewById(R.id.txtAge);
        completedWorkouts = findViewById(R.id.txtCompletedWorkouts);
        weight = findViewById(R.id.txtWeight);
        height = findViewById(R.id.txtHeight);
        logout = findViewById(R.id.btnLogout);
        changePassword = findViewById(R.id.txtChangePassword);
        caloriesBurned = findViewById(R.id.txtCaloriesBurned);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    void getUserData() {
        if (user != null) {
            // Get and display user information from Firebase Authentication
            String sName = user.getDisplayName();
            String sEmail = user.getEmail();
            String profilePictureUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

            name.setText(sName);
            email.setText(sEmail);

            if (profilePictureUrl != null) {
                Glide.with(this).load(profilePictureUrl).into(profileImage);
            } else {
            }
        }
    }
    void getFireStoreData() {
        if (user != null) {
            String uID = user.getUid();
            db.collection("users").document(uID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long ageLong = document.getLong("Age");
                                Long caloriesBurntLong = document.getLong("CaloriesBurnt");
                                String sGender = document.getString("Gender");
                                String goal = document.getString("Goal");
                                Long heightLong = document.getLong("Height");
                                Long weightLong = document.getLong("Weight");
                                Long workoutsCompletedLong = document.getLong("WorkoutsCompleted");

                                String sAge = (ageLong != null) ? String.valueOf(ageLong) : "";
                                String sCaloriesBurnt = (caloriesBurntLong != null) ? String.valueOf(caloriesBurntLong) : "";
                                String sHeight = (heightLong != null) ? String.valueOf(heightLong) : "";
                                String sWeight = (weightLong != null) ? String.valueOf(weightLong) : "";
                                String sWorkoutsCompleted = (workoutsCompletedLong != null) ? String.valueOf(workoutsCompletedLong) : "";

                                if (isProfileIncomplete(sGender, sAge, sWeight)) {
                                    showCompleteProfileDialog();
                                }

                                age.setText(sAge);
                                caloriesBurned.setText(sCaloriesBurnt);
                                gender.setText(sGender);
                                completedWorkouts.setText(sWorkoutsCompleted);
                                height.setText(sHeight);
                                weight.setText(sWeight);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
        }
    }

    private boolean isProfileIncomplete(String gender, String age, String weight) {
        return (gender == null || gender.isEmpty()) ||
                (age == null || Integer.parseInt(age) < 1) ||
                (weight == null || Integer.parseInt(weight) == 0);
    }

    private void showCompleteProfileDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Complete Your Profile")
                .setMessage("Your profile is incomplete. Please complete your profile.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Profile.this, AddUserDetails.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Profile.this, Main.class));
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    public static void showResetPasswordDialog(Context context, String emailAddress) {
        new AlertDialog.Builder(context)
                .setTitle("Reset Password")
                .setMessage("Are you sure you want to reset your password?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sendPasswordResetEmail(context, emailAddress);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private static void sendPasswordResetEmail(Context context, String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showConfirmationDialog(context);
                    } else {
                        Toast.makeText(context, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private static void showConfirmationDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Email Sent")
                .setMessage("Check your email to reset your password.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}