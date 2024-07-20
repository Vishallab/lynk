package com.vishal.mysocialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private EditText etName, etUsername, etEmail, etPassword;
    private CircleImageView ivAvatar;
    private Button btnChooseAvatar, btnSignUp;

    private TextView txtSignIn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Uri avatarUri;
    private ProgressDialog progressDialog;

    private TextInputLayout nameInputLayout, usernameInputLayout, emailInputLayout, passwordInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnChooseAvatar = findViewById(R.id.btnChooseAvatar);
        btnSignUp = findViewById(R.id.btnSignUp);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        usernameInputLayout = findViewById(R.id.InputLayoutusername);
        emailInputLayout = findViewById(R.id.InputLayoutEmail);
        passwordInputLayout = findViewById(R.id.InputLayoutPassword);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...\nPlease Wait.. Server is slow");
        progressDialog.setCancelable(false);

        btnChooseAvatar.setOnClickListener(v -> openImageChooser());

        btnSignUp.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = etEmail.getText().toString().trim();
                checkEmailExists(email);
            }
        });

        ImageView btnBack = findViewById(R.id.back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        txtSignIn = findViewById(R.id.txtSignIn);

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            avatarUri = data.getData();
            ivAvatar.setImageURI(avatarUri);
        }
    }

    private boolean validateInputs() {
        boolean valid = true;

        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            nameInputLayout.setError("This field is required");
            valid = false;
        } else {
            nameInputLayout.setError(null);
        }

        if (username.isEmpty()) {
            usernameInputLayout.setError("This field is required");
            valid = false;
        } else {
            usernameInputLayout.setError(null);
        }

        if (email.isEmpty()) {
            emailInputLayout.setError("This field is required");
            valid = false;
        } else {
            emailInputLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordInputLayout.setError("This field is required");
            valid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        if (avatarUri == null) {
            Toast.makeText(this, "Please select an avatar", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void checkEmailExists(String email) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                        if (emailExists) {
                            Toast.makeText(SignUpActivity.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                        } else {
                            signUpUser();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Error checking email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUpUser() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressDialog.show(); // Show progress dialog before starting sign-up process

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            if (avatarUri != null) {
                                uploadAvatar(avatarUri, userId, name, username, email);
                            } else {
                                saveUserDetails(userId, name, username, email, null);
                            }
                        }
                    } else {
                        progressDialog.dismiss(); // Dismiss progress dialog on failure
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign Up failed.";
                        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAvatar(Uri avatarUri, String userId, String name, String username, String email) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("avatars/" + userId + ".jpg");
        storageRef.putFile(avatarUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String avatarUrl = uri.toString();
                    saveUserDetails(userId, name, username, email, avatarUrl);
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss progress dialog on failure
                    Toast.makeText(SignUpActivity.this, "Avatar upload failed.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserDetails(String userId, String name, String username, String email, String avatarUrl) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("username", username);
        user.put("email", email);
        if (avatarUrl != null) {
            user.put("avatarUrl", avatarUrl);
        }

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss(); // Dismiss progress dialog on success
                    Toast.makeText(SignUpActivity.this, "Sign Up successful.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss progress dialog on failure
                    Toast.makeText(SignUpActivity.this, "Error saving user details.", Toast.LENGTH_SHORT).show();
                });
    }
}
