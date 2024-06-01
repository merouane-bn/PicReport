package com.example.myapplication.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddImage extends AppCompatActivity {
    RelativeLayout pickImageButton;
    ViewPager viewPager;
    Uri imageUri;
    ArrayList<Uri> ChooseImageList;
    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    Button uploadButton;
    List<String> UrlList;
    FirebaseAuth mAuth;
    EditText name, description;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        pickImageButton = findViewById(R.id.chooseImage);
        viewPager = findViewById(R.id.viewPager);
        ChooseImageList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        uploadButton = findViewById(R.id.btnUpload);
        name = findViewById(R.id.edtItemName);
        description = findViewById(R.id.edtItemDesc);
        UrlList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                PickImageFromaGallery();
            }


        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload images to firebase storage
                UploadImages();
            }
        });
    }

    private void UploadImages() {

        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri individualImage = ChooseImageList.get(i);
            if (individualImage != null) {
                progressDialog.show();
                StorageReference imageFolder = storageReference.child("ItemImages/" + individualImage.getLastPathSegment());
                final StorageReference ImageName = imageFolder.child("Image" + i+": "+individualImage.getLastPathSegment());
                ImageName.putFile(individualImage).addOnSuccessListener(taskSnapshot -> {
                    ImageName.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = String.valueOf(uri);
                        UrlList.add(url);
                        if (UrlList.size() == ChooseImageList.size()) {
                            // Save the URL to firestore
                            SaveUrlToFirestore(UrlList);
                        }
                    });
                });
            }
        }
    }

    private void SaveUrlToFirestore(List<String> urlList) {
        String userId = mAuth.getCurrentUser().getEmail();
        String nameText = name.getText().toString().trim();
        String descriptionText = description.getText().toString().trim();
        if (nameText.isEmpty()){
            name.setError("Name is required");
            progressDialog.dismiss();
        }
        else if (descriptionText.isEmpty()){
            description.setError("Description is required");
            progressDialog.dismiss();

        }
        else if (urlList.isEmpty()) {
            uploadButton.setError("Please select an image");
            progressDialog.dismiss();

        }
        else {
            ItemModel itemModel = new ItemModel(userId, nameText, descriptionText, (ArrayList<String>) urlList);
            String itemId = firestore.collection("Items").document().getId(); // Generate new ID
            itemModel.setItemId(itemId);
            firestore.collection("Items").document(itemId).set(itemModel, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();

                        // Pass itemId to the report activity
                        Intent reportIntent = new Intent(this, Repport.class);
                        reportIntent.putExtra("itemId", itemId);
                        startActivity(reportIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        ChooseImageList.clear();
    }

    private void checkPermission() {
        // Check if permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else {
                // Permission already granted
                PickImageFromaGallery();
            }
        }
    }

    private void PickImageFromaGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null){
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                imageUri = data.getClipData().getItemAt(i).getUri();
                // Do something with the image
                ChooseImageList.add(imageUri);
                SetAdapter();
            }
        }
    }

    private void SetAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, ChooseImageList);
        viewPager.setAdapter(viewPagerAdapter);
    }

}