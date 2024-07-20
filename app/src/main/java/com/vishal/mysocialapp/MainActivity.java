package com.vishal.mysocialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<DocumentSnapshot> postList;
    private TextView usernameHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList, mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null);
        recyclerViewPosts.setAdapter(postAdapter);

        usernameHead = findViewById(R.id.usernameHead);

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch and display username
            fetchAndDisplayUsername(currentUser);
        } else {
            // Redirect to login if not logged in
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        // Load posts
        loadPosts();

        ImageButton chatbtn = findViewById(R.id.chatbtn);
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });

        LinearLayout linearLayoutAdd = findViewById(R.id.addnav);
        linearLayoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddPostActivity.class));
            }
        });

        LinearLayout linearLayoutProfile = findViewById(R.id.profilenav);
        linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
    }

    private void fetchAndDisplayUsername(FirebaseUser currentUser) {
        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                if (username != null) {
                    usernameHead.setText(username);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error
        });
    }

    private void loadPosts() {
        CollectionReference postsRef = db.collection("posts");
        postsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
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
