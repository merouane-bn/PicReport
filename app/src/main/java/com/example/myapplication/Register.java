package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText name, email, password, confirmPassword;
    private Button register;
    private TextView loginRedirectText;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        checkAndAddAdmin();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.repassword);
        register = findViewById(R.id.register);
        loginRedirectText = findViewById(R.id.login);

        register.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String confirmPasswordText = confirmPassword.getText().toString().trim();
            if (nameText.isEmpty())
                name.setError("Name is required");
            else if (emailText.isEmpty())
                email.setError("Email is required");
            else if (passwordText.isEmpty())
                password.setError("Password is required");
            else if (!passwordText.equals(confirmPasswordText))
                confirmPassword.setError("Passwords do not match");
            else {
                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                String role = "user";  // Par défaut, nouvel utilisateur est un utilisateur régulier

                                // Enregistrement du rôle dans Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", nameText);
                                userMap.put("email", emailText);
                                userMap.put("role", role);

                                firestore.collection("users").document(userId).set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(Register.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Register.this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(Register.this, "SignUp Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        });
    }
    private void checkAndAddAdmin() {
        // Vérifie si un administrateur existe déjà
        firestore.collection("users").whereEqualTo("role", "admin").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // Aucun administrateur trouvé, ajouter un administrateur temporairement
                        String email = "admin@example.com"; // Remplacez par l'email de l'administrateur
                        String password = "password"; // Remplacez par le mot de passe de l'administrateur

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        String role = "admin";

                                        // Enregistrement du rôle dans Firestore
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email);
                                        userMap.put("role", role);

                                        firestore.collection("users").document(userId).set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this, "Admin Added Successfully", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(this, "Error adding admin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}