package com.vishal.mysocialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class UserChatActivity extends AppCompatActivity {

    private ImageView userAvatar;
    private TextView userName;
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        userAvatar = findViewById(R.id.userAvatar);
        userName = findViewById(R.id.userName);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Get data from intent
        String avatarUrl = getIntent().getStringExtra("avatarUrl");
        String name = getIntent().getStringExtra("name");
        String username = getIntent().getStringExtra("username");

        userName.setText(name);
        Glide.with(this).load(avatarUrl).into(userAvatar);

        // Setup RecyclerView
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Load messages and setup adapter (this part needs to be implemented)
        loadMessages(username);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sending message (this part needs to be implemented)
                sendMessage(username);
            }
        });
    }

    private void loadMessages(String username) {
        // Implementation for loading messages from Firebase
    }

    private void sendMessage(String username) {
        // Implementation for sending a message to Firebase
    }
}
