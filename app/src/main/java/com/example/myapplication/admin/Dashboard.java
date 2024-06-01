package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.UserAdapter;
import com.example.myapplication.Utils.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements UserAdapter.OnItemClickListener {
    private static final String TAG = "Dashboard";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize User List
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, (UserAdapter.OnItemClickListener) this);
        recyclerView.setAdapter(userAdapter);

        // Load users from Firestore
        loadUsers();
    }
    @Override
    public void onItemClick(User user) {
        // Handle item click event here, e.g., start UserDetailsActivity
        Intent intent = new Intent(this, UserDetails.class);
        intent.putExtra("uid", user.getEmail()); // Pass the user object to UserDetailsActivity if needed
        startActivity(intent);
    }
    private void loadUsers() {
        // Retrieve the currently authenticated user's email
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Retrieve all users except the currently authenticated user
        firestore.collection("users")
                .whereNotEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                        User user = userSnapshot.toObject(User.class);
                        // Check if the user has associated reports
                        firestore.collection("reports")
                                .whereEqualTo("userId", user.getEmail())
                                .limit(1) // Limit to one report (we only need to check if any reports exist)
                                .get()
                                .addOnSuccessListener(reportSnapshots -> {
                                    // If the user has at least one report, add them to the userList
                                    if (!reportSnapshots.isEmpty()) {
                                        userList.add(user);
                                        userAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error fetching reports for user: " + user.getEmail(), e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching users", e);
                    Toast.makeText(Dashboard.this, "Error fetching users", Toast.LENGTH_SHORT).show();
                });
    }
}
