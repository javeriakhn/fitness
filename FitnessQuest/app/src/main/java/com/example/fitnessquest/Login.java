package com.example.fitnessquest;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextView goToSignup, txtHeadingLogin, forgotPassword;
    TextInputLayout txtEmail, txtPassword;
    TextInputEditText edtEmail, edtPassword;
    ImageView imgLogo, imgAnimation;
    MaterialButton btnLogin;
    private FirebaseAuth mAuth;
    ConstraintLayout loginScreen;
    CircularProgressIndicator progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setVariables();
        setUpTextWatchers();
        loginScreen.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = getCurrentFocus();
                if (currentFocus instanceof TextInputEditText) {
                    Rect outRect = new Rect();
                    currentFocus.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        currentFocus.clearFocus();
                        hideKeyboard(v);
                    }
                }
            }
            return false;
        });

        // Adding global layout listener
        loginScreen.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            loginScreen.getWindowVisibleDisplayFrame(r);
            int screenHeight = loginScreen.getRootView().getHeight();
            // Check if the visible part of the screen is less than 80% of the total screen height.
            // If so, the keyboard is probably open.
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.2) { // If more than 20% of the screen height is covered, the keyboard is open
                // Keyboard is open
            } else {
                // Keyboard is closed
                if (getCurrentFocus() instanceof TextInputEditText) {
                    getCurrentFocus().clearFocus();
                    hideKeyboard(loginScreen);
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            // Resetting errors
            txtEmail.setError(null);
            txtPassword.setError(null);
            progressBar.setVisibility(View.VISIBLE);
            hideKeyboard(v);

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Checking if email or password fields are empty
            if (email.isEmpty()) {
                txtEmail.setError(getString(R.string.empty_email_error));
                progressBar.setVisibility(View.GONE);
                return; // Stop further execution if email is empty
            }

            if (password.isEmpty()) {
                txtPassword.setError(getString(R.string.empty_password_error));
                progressBar.setVisibility(View.GONE);
                return; // Stop further execution if password is empty
            }
            if (!isValidEmail(email)) {
                txtEmail.setError(getString(R.string.invalid_email_error));
                progressBar.setVisibility(View.GONE);
                return; // Stop further execution if email is invalid
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            user.getUid();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    });
                });

        goToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup.class);
            Pair[] pairs = new Pair[7];
            pairs[0] = new Pair<View, String>(imgLogo, "logo_image");
            pairs[1] = new Pair<View, String>(imgAnimation, "animation_image");
            pairs[2] = new Pair<View, String>(txtHeadingLogin, "txtHeading");
            pairs[3] = new Pair<View, String>(txtEmail, "txtEmail");
            pairs[4] = new Pair<View, String>(txtPassword, "txtPassword");
            pairs[5] = new Pair<View, String>(goToSignup, "txtSwitch");
            pairs[6] = new Pair<View, String>(btnLogin, "btnMain");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent, options.toBundle());
        });

        forgotPassword.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            // Resetting errors
            txtEmail.setError(null);

            String email = edtEmail.getText().toString().trim();

            // Checking if email or password fields are empty
            if (email.isEmpty()) {
                txtEmail.setError(getString(R.string.empty_email_error));
                progressBar.setVisibility(View.GONE);
                return; // Stop further execution if email is empty
            }
            if (!isValidEmail(email)) {
                txtEmail.setError(getString(R.string.invalid_email_error));
                progressBar.setVisibility(View.GONE);
                return; // Stop further execution if email is invalid
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showDialog("Reset password email sent", "Please check your email for a password reset link.");
                        } else {
                            showDialog("Error", "Failed to send reset email. Please try again later.");
                        }
                    });
        });
    }
    void setVariables(){
        goToSignup = findViewById(R.id.txtGoToSignup);
        imgLogo = findViewById(R.id.imgLogoLogin);
        imgAnimation = findViewById(R.id.imgAnimation);
        txtHeadingLogin = findViewById(R.id.txtHeadingLogin);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.inputEmail);
        edtPassword = findViewById(R.id.inputPassword);
        loginScreen = findViewById(R.id.loginScreen);
        progressBar = findViewById(R.id.progressBarLogin);
        forgotPassword = findViewById(R.id.txtForgotPassword);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }
    private void updateUI(FirebaseUser user) {
        progressBar.setVisibility(View.GONE);
        if (user != null) {
            // User is signed in, navigate to the MainActivity
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            finish(); // Close the login activity
        } else {
            // User sign-in failed, show a dialog with the error
            showDialog(getString(R.string.login_failed_title), getString(R.string.auth_failed_message));
        }
    }
    private void showDialog(String title, String message) {
        new MaterialAlertDialogBuilder(Login.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtEmail.setText("");
        edtPassword.setText("");
    }

    private void setUpTextWatchers() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Reset error as user types
                if (txtEmail.getError() != null) {
                    txtEmail.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement this
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Reset error as user types
                if (txtPassword.getError() != null) {
                    txtPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Hides the soft keyboard.
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}