package com.example.myapplication.user;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Repport extends AppCompatActivity {
    private Button buttonGenerateReport;
    private String itemId, name, description, userId;
    private ArrayList<String> imageUrls;
    private ArrayList<File> downloadedImages;

    private FirebaseFirestore db;
    private StorageReference storageReference;
    Document document;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repport);

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize views and get data from previous activity
        buttonGenerateReport = findViewById(R.id.buttonSubmit);
        itemId = getIntent().getStringExtra("itemId");

        buttonGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchItem();
            }
        });
    }

    public void fetchItem() {
        db.collection("Items")
                .document(itemId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Item exists, retrieve its data
                            name = documentSnapshot.getString("finalNameText");
                            description = documentSnapshot.getString("finalDescriptionText");
                            userId = documentSnapshot.getString("userId");
                            // Assuming imageUrls is stored as an ArrayList in Firestore
                            imageUrls = (ArrayList<String>) documentSnapshot.get("url");

                            // Generate PDF report
                            generatePDF(name, description, userId, imageUrls);
                        } else {
                            // Item with given itemId does not exist
                            Toast.makeText(Repport.this, "Item not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while fetching item
                        Toast.makeText(Repport.this, "Failed to fetch item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generatePDF(String name, String description, String userId, ArrayList<String> imageUrls) {
        // Create a PDF document
        try {
            // Create a file to save the PDF
            File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "report"+System.currentTimeMillis()+".pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            // Create a Document instance using the PdfDocument
            document = new Document(pdfDocument);

            // Add title to the PDF (centered)
            Style titleStyle = new Style()
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph title = new Paragraph("Report").addStyle(titleStyle);
            document.add(title);

            // Add content to the PDF
            document.add(new Paragraph("Name: " + name));
            document.add(new Paragraph("Description: " + description));
            document.add(new Paragraph("User ID: " + userId));

            // Download images temporarily and add them to the PDF
            downloadedImages = new ArrayList<>();
            int totalImages = imageUrls.size();
            int imagesAdded = 0;
            for (String imageUrl : imageUrls) {
                downloadImageAsync(imageUrl, pdfFile, ++imagesAdded, totalImages);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void downloadImageAsync(final String imageUrl, final File pdfFile, final int imagesAdded, final int totalImages) {
        new AsyncTask<Void, Void, File>() {
            @Override
            protected File doInBackground(Void... voids) {
                try {
                    // Extract filename from URL
                    String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                    // Sanitize filename
                    filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");

                    // Create a temporary file for the image
                    File tempFile = File.createTempFile("image_", filename);
                    FileOutputStream fos = new FileOutputStream(tempFile);

                    // Download the image from the URL
                    URL url = new URL(imageUrl);
                    InputStream inputStream = url.openStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    inputStream.close();

                    // Log success
                    Log.d("DownloadImage", "Image downloaded successfully from URL: " + imageUrl);

                    return tempFile;
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log error
                    Log.e("DownloadImage", "Failed to download image from URL: " + imageUrl, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File result) {
                if (result != null) {
                    // Image downloaded successfully, do something with the file
                    // For example, add it to the PDF document
                    addImageToPDF(result);
                    if (imagesAdded == totalImages) {
                        document.close();

                        // Upload the PDF to Firebase Storage
                        uploadPDF(pdfFile);
                    }
                } else {
                    // Image download failed, handle accordingly
                }
            }
        }.execute();
    }

    private void addImageToPDF(File imageFile) {
        // Add the image to the PDF document
        try {
            // Load the image
            ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
            Image image = new Image(imageData);

            // Set the maximum width and height of the image in points (1 inch = 72 points)
            float maxWidth = 400; // Adjust this value as needed
            float maxHeight = 300; // Adjust this value as needed

            // Calculate the scaling factor to fit the image within the maximum width and height
            float scaleX = maxWidth / image.getImageWidth();
            float scaleY = maxHeight / image.getImageHeight();

            // Determine the scaling factor that preserves the aspect ratio of the image
            float scaleFactor = Math.min(scaleX, scaleY);

            // Scale the image
            image.scaleToFit(image.getImageWidth() * scaleFactor, image.getImageHeight() * scaleFactor);

            // Add the scaled image to the PDF document
            document.add(image);
        } catch (Exception e) {
            e.printStackTrace();
            // Log error
            Log.e("AddImageToPDF", "Failed to add image to PDF", e);
        }
    }

    private void addReportToFirestore(String userId, String pdfUrl) {
        // Get the current date
        Date currentDate = new Date();

        // Create a new report object with userId, date, and PDF URL
        Map<String, Object> report = new HashMap<>();
        report.put("userId", userId);
        report.put("date", currentDate);
        report.put("pdfUrl", pdfUrl);

        // Add the report to the "reports" collection
        db.collection("reports")
                .add(report)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AddReport", "Report added to Firestore: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AddReport", "Failed to add report to Firestore: " + e.getMessage());
                    }
                });
    }

    private void uploadPDF(File pdfFile) {
        // Upload the PDF to Firebase Storage
        StorageReference pdfRef = storageReference.child("reports/" + pdfFile.getName());
        pdfRef.putFile(Uri.fromFile(pdfFile))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded PDF
                        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String pdfUrl = uri.toString();
                                // Add the report data to Firestore
                                addReportToFirestore(userId, pdfUrl);
                                // Show the download PDF dialog
                                showDownloadPdfDialog(pdfUrl);

                            }
                        });

                        // Delete temporary image files
                        for (File imageFile : downloadedImages) {
                            imageFile.delete();
                        }
                        Toast.makeText(Repport.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Repport.this, "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showDownloadPdfDialog(String pdfUrl) {
        DownloadPdf dialogFragment = DownloadPdf.newInstance(pdfUrl);
        dialogFragment.show(getSupportFragmentManager(), "DownloadPdfDialog");
    }

}
