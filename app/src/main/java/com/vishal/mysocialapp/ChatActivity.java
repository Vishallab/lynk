package com.vishal.mysocialapp;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatUserAdapter chatUserAdapter;
    private List<ChatUser> chatUserList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatUserList = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(this, chatUserList);
        recyclerView.setAdapter(chatUserAdapter);

        db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    chatUserList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ChatUser chatUser = document.toObject(ChatUser.class);
                        chatUserList.add(chatUser);
                    }
                    chatUserAdapter.notifyDataSetChanged();
                } else {
                    // Handle the error
                    Log.d("ChatActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
