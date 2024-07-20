package com.vishal.mysocialapp;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    private List<ChatUser> chatUserList;
    private Context context;

    public ChatUserAdapter(Context context, List<ChatUser> chatUserList) {
        this.context = context;
        this.chatUserList = chatUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatUser chatUser = chatUserList.get(position);
        holder.userName.setText(chatUser.getName());
        // Load the avatar using Glide or Picasso
        Glide.with(context)
                .load(chatUser.getAvatarUrl())
                .into(holder.userImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserChatActivity.class);
                intent.putExtra("avatarUrl", chatUser.getAvatarUrl());
                intent.putExtra("name", chatUser.getName());
                intent.putExtra("username", chatUser.getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImg);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
