package com.example.fitnessquest;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    TextView goToLogin, txtHeadingSignup;
    ImageView imgLogoSignup, imgAnimationSignup;
    ConstraintLayout signupScreen;
    TextInputEditText edtEmail, edtPassword, edtFullname;
    TextInputLayout txtEmail, txtPassword, txtFullname;
    MaterialButton btnSignup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signupScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setVariables();
        setUpTextWatchers();

        btnSignup.setOnClickListener(v -> {
            // Resetting errors
            txtEmail.setError(null);
            txtPassword.setError(null);
            txtFullname.setError(null);
            hideKeyboard(v);

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String fullname = edtFullname.getText().toString().trim();
            // Checking if fullname is empty
            if (fullname.isEmpty()) {
                txtFullname.setError(getString(R.string.empty_fullname_error));
                return; // Stop further execution if fullname is empty
            }
            if (fullname.length() < 5) {
                txtFullname.setError(getString(R.string.short_fullname_error));
                return; // Stop further execution if fullname is empty
            }
            if (isValidName(fullname)) {
                txtFullname.setError(getString(R.string.invalid_fullname_error));
                return; // Stop further execution if fullname is empty
            }
            // Checking if email or password fields are empty
            if (email.isEmpty()) {
                txtEmail.setError(getString(R.string.empty_email_error));
                return; // Stop further execution if email is empty
            }
            if (password.isEmpty()) {
                txtPassword.setError(getString(R.string.empty_password_error));
                return; // Stop further execution if password is empty
            }
            if (password.length() < 6) {
                txtPassword.setError(getString(R.string.invalid_password_error));
                return; // Stop further execution if password is empty
            }
            if (!isValidEmail(email)) {
                txtEmail.setError(getString(R.string.invalid_email_error));
                return; // Stop further execution if email is invalid
            }

            // Create a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.getUid();
                            // Send verification email
                            if (user != null) {

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullname)
                                        .build();

                                user.sendEmailVerification()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {

                                                Log.d("EmailVerification", "Email sent.");
                                                showDialog(getString(R.string.email_verification_title), getString(R.string.email_verification_message));
                                                edtEmail.setText("");
                                                edtPassword.setText("");
                                                edtFullname.setText("");
                                                FirebaseAuth.getInstance().signOut();
                                            } else {
                                                Log.w("EmailVerification", "sendEmailVerification", task1.getException());
                                            }
                                        });
                                createUserDocument(user);

                            }
                        } else {
                            // If sign up fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                showDialog(getString(R.string.signup_failed_title),getString(R.string.signup_email_exists));
                            } else {
                                Toast.makeText(Signup.this, "Authentication failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                        }
                    });
        });

        //Takes user to login screen
        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            //Transition animation
            Pair[] pairs = new Pair[7];
            pairs[0] = new Pair<View, String>(imgLogoSignup, "logo_image");
            pairs[1] = new Pair<View, String>(imgAnimationSignup, "animation_image");
            pairs[2] = new Pair<View, String>(txtHeadingSignup, "txtHeading");
            pairs[3] = new Pair<View, String>(txtEmail, "txtEmail");
            pairs[4] = new Pair<View, String>(txtPassword, "txtPassword");
            pairs[5] = new Pair<View, String>(goToLogin, "txtSwitch");
            pairs[6] = new Pair<View, String>(btnSignup, "btnMain");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup.this, pairs);
            startActivity(intent, options.toBundle());
        });
        //Hide keyboard and clear focus on screen click
        signupScreen.setOnTouchListener((v, event) -> {
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

        // Adding global layout listener to check if the keyboard is open or closed.
        signupScreen.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            signupScreen.getWindowVisibleDisplayFrame(r);
            int screenHeight = signupScreen.getRootView().getHeight();
            // Check if the visible part of the screen is less than 80% of the total screen height.
            // If so, the keyboard is probably open.
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.2) { // If more than 20% of the screen height is covered, the keyboard is open
                // Keyboard is open
            } else {
                // Keyboard is closed
                if (getCurrentFocus() instanceof TextInputEditText) {
                    getCurrentFocus().clearFocus();
                    hideKeyboard(signupScreen);
                }
            }
        });
    }

    private void createUserDocument(FirebaseUser user) {
        String uID = user.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("uID", uID);
        userData.put("Gender", "");
        userData.put("Age", 0);
        userData.put("CaloriesBurnt", 0);
        userData.put("Height", 0);
        userData.put("Weight", 0);
        userData.put("Goal", "");
        userData.put("ActiveStatus", "");
        userData.put("WorkoutsCompleted", 0);
        userData.put("premiumStatus", false);

        db.collection("users").document(uID)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User document created successfully.");
                    } else {
                        Log.w(TAG, "Error creating user document", task.getException());
                    }
                });
    }
    void setVariables() {
        goToLogin = findViewById(R.id.txtGoToLogin);
        txtHeadingSignup = findViewById(R.id.txtHeadingSignup);
        imgLogoSignup = findViewById(R.id.imgLogoSignup);
        imgAnimationSignup = findViewById(R.id.imgAnimationSignup);
        signupScreen = findViewById(R.id.signupScreen);
        txtEmail = findViewById(R.id.txtEmailSignup);
        txtPassword = findViewById(R.id.txtPasswordSignup);
        txtFullname = findViewById(R.id.txtFullname);
        btnSignup = findViewById(R.id.btnSignup);
        edtEmail = findViewById(R.id.inputEmailSignup);
        edtPassword = findViewById(R.id.inputPasswordSignup);
        edtFullname = findViewById(R.id.inputFullname);
        imgLogoSignup = findViewById(R.id.imgLogoSignup);
        imgAnimationSignup = findViewById(R.id.imgAnimationSignup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    private void setUpTextWatchers() {
        edtFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Reset error as user types
                if (txtFullname.getError() != null) {
                    txtFullname.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
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
    private void showDialog(String title,String message) {
        new MaterialAlertDialogBuilder(this, R.style.CustomDialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    // Respond to positive button press, possibly reset login form or just dismiss dialog
                    dialog.dismiss();
                })
                .show();
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public boolean isValidName(String text) {
        // Regular expression that matches any character that is not a letter, digit, or space
        String regex = "[^a-zA-Z0-9\\s]";
        if (text.matches(".*" + regex + ".*")) {
            return true;
        }
        return false;
    }
    //Hides the soft keyboard.
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtEmail.setText("");
        edtPassword.setText("");
        edtFullname.setText("");
    }
}