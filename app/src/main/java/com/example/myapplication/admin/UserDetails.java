package com.example.myapplication.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Report;
import com.example.myapplication.Utils.ReportAdapter;
import com.example.myapplication.Utils.UserAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserDetails extends AppCompatActivity implements ReportAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        String userEmail = getIntent().getStringExtra("uid");
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvreports);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize User List
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this, reportList,  this);
        recyclerView.setAdapter(reportAdapter);
        loadUserReports(userEmail);
    }

    private void loadUserReports(String userEmail) {
        // Retrieve reports from Firestore
        firestore.collection("reports")
                .whereEqualTo("userId", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        Report report = queryDocumentSnapshots.getDocuments().get(i).toObject(Report.class);
                        reportList.add(report);
                    }
                    reportAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onItemClick(Report report) {
        Toast.makeText(this, "Report URL: " + report.getpdfUrl(), Toast.LENGTH_SHORT).show();
        // Handle item click event here, e.g., download the report by its URL
        String reportUrl = report.getpdfUrl();
        if (reportUrl != null && !reportUrl.isEmpty()) {
            // Ensure reportUrl is not null or empty before parsing
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reportUrl));
            startActivity(browserIntent);
        } else {
            // Handle the case where reportUrl is null or empty
            Toast.makeText(this, "Report URL is not available", Toast.LENGTH_SHORT).show();
        }

    }
}