package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.admin.Dashboard;
import com.example.myapplication.user.AddImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    EditText email, password;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        login.setOnClickListener(view -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.setError("Valid email is required");
                return;
            }
            if (passwordText.isEmpty()) {
                password.setError("Password is required");
                return;
            }

            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnSuccessListener(authResult -> {
                        checkUserRole(authResult.getUser().getUid());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        register.setOnClickListener(view -> {
            startActivity(new Intent(this, Register.class));
            finish();
        });
    }

    private void checkUserRole(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            startActivity(new Intent(MainActivity.this, Dashboard.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, AddImage.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}