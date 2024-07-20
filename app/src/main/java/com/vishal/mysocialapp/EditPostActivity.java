package com.vishal.mysocialapp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.UUID;
public class EditPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivSelectedImage;
    private EditText etCaption;
    private Button btnSave;
    private ImageButton btnSelectImage;

    private ProgressDialog progressDialog;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String postId;
    private String currentImageUrl;
    private Uri newImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        etCaption = findViewById(R.id.etCaption);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");

        // Load current post data


        loadPostData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...\nPlease Wait.. Server is slow");
        progressDialog.setCancelable(false);



        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> {

            progressDialog.show();
            savePostChanges();
        });



        ImageButton closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current activity and go back to the previous one
                finish();
            }
        });


    }

    private void loadPostData() {
        db.collection("posts").document(postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String caption = task.getResult().getString("caption");
                currentImageUrl = task.getResult().getString("imageUrl");

                etCaption.setText(caption);

                if (currentImageUrl != null) {
                    Glide.with(this).load(currentImageUrl).into(ivSelectedImage);
                }
            } else {
                Toast.makeText(EditPostActivity.this, "Failed to load post data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(newImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                ivSelectedImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("EditPostActivity", "Error loading image", e);
            }
        }
    }

    private void savePostChanges() {
        String newCaption = etCaption.getText().toString().trim();

        if (newCaption.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(EditPostActivity.this, "Caption cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newImageUri != null) {
            // Handle image upload
            uploadImageAndSavePost(newCaption);
        } else {
            // No new image selected, only update caption
            updatePost(newCaption, currentImageUrl);
        }
    }

    private void uploadImageAndSavePost(String caption) {
        final StorageReference imageRef = storageReference.child("post_images/" + UUID.randomUUID().toString());

        UploadTask uploadTask = imageRef.putFile(newImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            progressDialog.dismiss();

            String newImageUrl = uri.toString();
            updatePost(caption, newImageUrl);
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();

            Toast.makeText(EditPostActivity.this, "Error getting image URL", Toast.LENGTH_SHORT).show();
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();

            Toast.makeText(EditPostActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
        });
    }

    private void updatePost(String caption, String imageUrl) {
        // Create a formatted timestamp with "edited at" text
        String timestamp = new java.text.SimpleDateFormat("hh:mm - dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        String editedTimestamp = "Edited at " + timestamp;

        // Update the post in Firestore
        db.collection("posts").document(postId).update("caption", caption, "imageUrl", imageUrl, "timestamp", editedTimestamp)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();

                    Toast.makeText(EditPostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();

                    Toast.makeText(EditPostActivity.this, "Error updating post", Toast.LENGTH_SHORT).show();
                });
    }

}
