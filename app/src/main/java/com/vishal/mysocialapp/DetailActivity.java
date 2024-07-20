package com.vishal.mysocialapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String postId;

    private ImageView ivAvatar, ivPostImage,btnSendComment;
    private TextView tvName, tvUsername, tvCaption, tvTimestamp, tvLikeCount, tvCommentCount;
    private EditText etComment;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");

        // Initialize views
        ivAvatar = findViewById(R.id.ivAvatar);
        ivPostImage = findViewById(R.id.ivPostImage);
        tvName = findViewById(R.id.name_detail);
        tvUsername = findViewById(R.id.tvUsername);
        tvCaption = findViewById(R.id.tvCaption);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvLikeCount = findViewById(R.id.count_likes);
        tvCommentCount = findViewById(R.id.count_comments);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        recyclerView = findViewById(R.id.recyclerView_detail);

        // Initialize comment list and adapter
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);

        // Load Post details
        loadPostDetails();

        // Load comments
        loadComments();

        // Send comment button click listener
        btnSendComment.setOnClickListener(v -> sendComment());
    }

    private void loadPostDetails() {
        db.collection("posts").document(postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot postSnapshot = task.getResult();
                if (postSnapshot.exists()) {
                    String userId = postSnapshot.getString("userId");
                    String caption = postSnapshot.getString("caption");
                    String imageUrl = postSnapshot.getString("imageUrl");
                    String timestamp = postSnapshot.getString("timestamp");
                    long likeCount = postSnapshot.getLong("likeCount") != null ? postSnapshot.getLong("likeCount") : 0;
                    long commentCount = postSnapshot.getLong("commentCount") != null ? postSnapshot.getLong("commentCount") : 0;

                    // Set Post details
                    tvCaption.setText(caption);
                    tvTimestamp.setText(timestamp);
                    tvLikeCount.setText(String.valueOf(likeCount));
                    tvCommentCount.setText(String.valueOf(commentCount));
                    Glide.with(this).load(imageUrl).into(ivPostImage);

                    // Load user details
                    if (userId != null) {
                        loadUserDetails(userId);
                    }
                }
            } else {
                Toast.makeText(this, "Error loading Post details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails(String userId) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot userSnapshot = task.getResult();
                if (userSnapshot.exists()) {
                    String name = userSnapshot.getString("name");
                    String username = userSnapshot.getString("username");
                    String avatarUrl = userSnapshot.getString("avatarUrl");

                    // Set user details
                    tvName.setText(name);
                    tvUsername.setText(username);
                    Glide.with(this).load(avatarUrl).into(ivAvatar);
                }
            } else {
                Toast.makeText(this, "Error loading user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        CollectionReference commentsRef = db.collection("posts").document(postId).collection("comments");
        commentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                commentList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Comment comment = document.toObject(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();

                // Update comment count
                updateCommentCount();
            } else {
                Toast.makeText(this, "Error loading comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCommentCount() {
        CollectionReference commentsRef = db.collection("posts").document(postId).collection("comments");
        commentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long commentCount = task.getResult().size();
                tvCommentCount.setText(String.valueOf(commentCount));

                // Update comment count in Firestore
                db.collection("posts").document(postId).update("commentCount", commentCount)
                        .addOnCompleteListener(updateTask -> {
                            if (!updateTask.isSuccessful()) {
                                Toast.makeText(this, "Error updating comment count.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Error updating comment count.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment() {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user details
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot userSnapshot = task.getResult();
                if (userSnapshot.exists()) {
                    String username = userSnapshot.getString("username");
                    String avatarUrl = userSnapshot.getString("avatarUrl");

                    // Create comment object
                    Comment comment = new Comment(userId, username, avatarUrl, commentText);

                    // Add comment to Firestore
                    CollectionReference commentsRef = db.collection("posts").document(postId).collection("comments");
                    commentsRef.add(comment).addOnCompleteListener(commentTask -> {
                        if (commentTask.isSuccessful()) {
                            etComment.setText("");
                            Toast.makeText(this, "Comment added.", Toast.LENGTH_SHORT).show();
                            loadComments(); // Refresh comments
                        } else {
                            Toast.makeText(this, "Error adding comment.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
