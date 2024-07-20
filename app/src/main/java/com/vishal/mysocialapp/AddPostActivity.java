    package com.vishal.mysocialapp;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.RecyclerView;

    import com.bumptech.glide.Glide;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;

    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Locale;
    import java.util.Map;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class AddPostActivity extends AppCompatActivity {

        private static final int PICK_IMAGE = 1;
        private EditText etCaption;
        private ImageView ivPostImage;
        private ImageButton btnChooseImage;
        private Button  btnPost;
        private Uri imageUri;
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;
        private ProgressDialog progressDialog;

        private CircleImageView circleImageView;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_post);

            etCaption = findViewById(R.id.etCaption);
            ivPostImage = findViewById(R.id.ivPostImage);
            btnChooseImage = findViewById(R.id.btnChooseImage);
            btnPost = findViewById(R.id.btnPost);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Posting...\nPlease Wait.. Server is slow");
            progressDialog.setCancelable(false);

            circleImageView=findViewById(R.id.avatarImageView);


            btnChooseImage.setOnClickListener(v -> openImageChooser());

            btnPost.setOnClickListener(v -> {
                String caption = etCaption.getText().toString().trim();
                if (caption.isEmpty() || imageUri == null) {
                    Toast.makeText(AddPostActivity.this, "Please enter a caption and choose an image", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                uploadPostImage(imageUri, caption);
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

        private void openImageChooser() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                ivPostImage.setImageURI(imageUri);
            }
        }

        private void uploadPostImage(Uri imageUri, String caption) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("post_images/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        savePostDetails(caption, imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddPostActivity.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
                    });
        }

        private void savePostDetails(String caption, String imageUrl) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                progressDialog.dismiss();
                Toast.makeText(AddPostActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            String userEmail = user.getEmail();
            String timestamp = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(new Date());

            // Create a map with post details, including the new commentCount field
            Map<String, Object> post = new HashMap<>();
            post.put("caption", caption);
            post.put("imageUrl", imageUrl);
            post.put("timestamp", timestamp);
            post.put("userEmail", userEmail);
            post.put("userId", userId);
            post.put("likeCount", 0); // Initialize likeCount
            post.put("commentCount", 0); // Initialize commentCount

            // Add the post to Firestore
            db.collection("posts").add(post)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddPostActivity.this, "Post created successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPostActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddPostActivity.this, "Error saving Post details.", Toast.LENGTH_SHORT).show();
                    });
        }







    }
