package com.vishal.mysocialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CircleImageView civAvatar;
    private TextView tvName, tvUsername;
    private Button btnLogout;
    private ListenerRegistration userListener;

    private RecyclerView profilePostsRecycler;
    private PostAdapter postAdapter;
    private List<DocumentSnapshot> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        civAvatar = findViewById(R.id.civAvatar);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);
        profilePostsRecycler = findViewById(R.id.profile_posts_recycler);

        // Initialize RecyclerView
        profilePostsRecycler.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList, mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null);
        profilePostsRecycler.setAdapter(postAdapter);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        loadUserProfile();
        loadUserPosts();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            userListener = db.collection("users").document(userId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Toast.makeText(ProfileActivity.this, "Error while loading user details.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String username = documentSnapshot.getString("username");
                            String avatarUrl = documentSnapshot.getString("avatarUrl");

                            tvName.setText(name);
                            tvUsername.setText("@" + username);
                            Glide.with(ProfileActivity.this).load(avatarUrl).into(civAvatar);
                        }
                    });
        }
    }

    private void loadUserPosts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            CollectionReference postsRef = db.collection("posts");
            postsRef.whereEqualTo("userId", userId)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(ProfileActivity.this, "Error while loading posts.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (queryDocumentSnapshots != null) {
                                postList.clear();
                                postList.addAll(queryDocumentSnapshots.getDocuments());
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userListener != null) {
            userListener.remove();
        }
    }
}
