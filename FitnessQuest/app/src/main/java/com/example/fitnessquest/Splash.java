package com.example.fitnessquest;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;

public class Splash extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView title, subTitle;
    Button getStarted;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Check if splash screen has been displayed
        if (!prefs.getBoolean("splashDisplayed", false)) {
            setVariables();
            setAnimations();
            getCurrentUser();

            getStarted.setOnClickListener(v -> {
                // Set the flag to true on button click
                prefs.edit().putBoolean("splashDisplayed", true).apply();
                Intent intent = new Intent(Splash.this, Login.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logo, "logo_image");
                pairs[1] = new Pair<View, String>(getStarted, "main_btn");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splash.this, pairs);
                // Start the Login activity
                startActivity(intent, options.toBundle());
            });
        }
        else {
            Intent intent = new Intent(Splash.this, Login.class);
            startActivity(intent);
        }
    }

    public void setVariables() {
        logo = findViewById(R.id.logo);
        title = findViewById(R.id.title);
        subTitle = findViewById(R.id.subTitle);
        getStarted = findViewById(R.id.getStarted);
    }
    public void setAnimations(){
        //Animations
        //topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        //Set animation to elements
        //logo.setAnimation(topAnim);
        title.setAnimation(bottomAnim);
        subTitle.setAnimation(bottomAnim);
        getStarted.setAnimation(bottomAnim);
    }

    public void getCurrentUser() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(Splash.this, Main.class);
            startActivity(intent);
        }
    }
}
