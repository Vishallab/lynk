
package com.vishal.mysocialapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<DocumentSnapshot> postList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserId;

    public PostAdapter(Context context, List<DocumentSnapshot> postList, String currentUserId) {
        this.context = context;
        this.postList = postList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        DocumentSnapshot post = postList.get(position);

        // Start shimmer animation
        holder.shimmerLayout.startShimmer();
        holder.shimmerLayout.setVisibility(View.VISIBLE);
        holder.postContent.setVisibility(View.GONE);

        String postId = post.getId();
        String userId = post.getString("userId");
        String caption = post.getString("caption");
        String imageUrl = post.getString("imageUrl");
        String timestamp = post.getString("timestamp");
        int likeCount = post.getLong("likeCount") != null ? post.getLong("likeCount").intValue() : 0;
        int commentCount = post.getLong("commentCount") != null ? post.getLong("commentCount").intValue() : 0;

        // Load post details
        holder.tvCaption.setText(caption);
        holder.tvTimestamp.setText(timestamp);
        Glide.with(context).load(imageUrl).into(holder.ivPostImage);
        holder.tvLikeCount.setText(String.valueOf(likeCount));
        holder.tvCommentCount.setText(String.valueOf(commentCount)); // Set comment count


        // Set initial like icon
        updateLikeIcon(holder, postId);

        // Fetch user details
        if (userId != null) {
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot userDocument = task.getResult();
                            if (userDocument.exists()) {
                                String name = userDocument.getString("name");
                                String username = userDocument.getString("username");
                                String avatarUrl = userDocument.getString("avatarUrl");

                                holder.tvName.setText(name);
                                holder.tvUsername.setText(username);

                                Glide.with(context).load(avatarUrl).into(holder.ivAvatar);
                                // Stop shimmer animation
                                holder.shimmerLayout.stopShimmer();
                                holder.shimmerLayout.setVisibility(View.GONE);
                                holder.postContent.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        holder.ivLike.setOnClickListener(v -> {
            toggleLike(postId, holder);
        });

        holder.ivPostImage.setOnClickListener(v -> {
            openDetailActivity(postId);
        });

        holder.commentIcon.setOnClickListener(v -> {
            openDetailActivity(postId);
        });

        holder.btnMoreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnMoreOptions);
            popupMenu.getMenuInflater().inflate(R.menu.menu_post_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    if (currentUserId.equals(userId)) {
                        Intent intent = new Intent(context, EditPostActivity.class);
                        intent.putExtra("postId", postId);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "You cannot edit this post", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    if (currentUserId.equals(userId)) {
                        deletePost(postId);
                    } else {
                        Toast.makeText(context, "You cannot delete this post", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        addPostListener(postId, holder);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void updateLikeIcon(PostViewHolder holder, String postId) {
        db.collection("posts").document(postId).collection("likes").document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isLiked = task.getResult().exists();
                        holder.ivLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                    } else {
                        Toast.makeText(context, "Error checking like status.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleLike(String postId, PostViewHolder holder) {
        db.collection("posts").document(postId).collection("likes").document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isLiked = task.getResult().exists();
                        if (isLiked) {
                            // Unliking the post
                            db.collection("posts").document(postId).collection("likes").document(currentUserId).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        updateLikeCount(postId, -1);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error unliking post.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Liking the post
                            Map<String, Object> like = new HashMap<>();
                            db.collection("posts").document(postId).collection("likes").document(currentUserId).set(like)
                                    .addOnSuccessListener(aVoid -> {
                                        updateLikeCount(postId, 1);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error liking post.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error checking like status.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateLikeCount(String postId, int change) {
        db.collection("posts").document(postId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot postSnapshot = task.getResult();
                        long currentLikeCount = postSnapshot.getLong("likeCount") != null ? postSnapshot.getLong("likeCount") : 0;
                        long newLikeCount = currentLikeCount + change;

                        if (newLikeCount < 0) {
                            newLikeCount = 0;
                        }

                        db.collection("posts").document(postId).update("likeCount", newLikeCount)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error updating like count.", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void addPostListener(String postId, PostViewHolder holder) {
        db.collection("posts").document(postId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                String caption = snapshot.getString("caption");
                String imageUrl = snapshot.getString("imageUrl");
                String timestamp = snapshot.getString("timestamp");
                long likeCount = snapshot.getLong("likeCount") != null ? snapshot.getLong("likeCount") : 0;
                long commentCount = snapshot.getLong("commentCount") != null ? snapshot.getLong("commentCount") : 0;

                holder.tvCaption.setText(caption);
                holder.tvTimestamp.setText(timestamp);
                Glide.with(context).load(imageUrl).into(holder.ivPostImage);
                holder.tvLikeCount.setText(String.valueOf(likeCount));
                holder.tvCommentCount.setText(String.valueOf(commentCount)); // Update comment count

                updateLikeIcon(holder, postId);
            }
        });
    }

    private void deletePost(String postId) {
        db.collection("posts").document(postId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting post.", Toast.LENGTH_SHORT).show();
                });
    }

    private void openDetailActivity(String postId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage, ivLike, commentIcon;
        TextView tvName, tvUsername, tvCaption, tvTimestamp, tvLikeCount, tvCommentCount;
        ImageButton btnMoreOptions;
        ShimmerFrameLayout shimmerLayout;
        View postContent;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivLike = itemView.findViewById(R.id.like_icon);
            commentIcon = itemView.findViewById(R.id.comment_icon);
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvLikeCount = itemView.findViewById(R.id.count_likes);
            tvCommentCount = itemView.findViewById(R.id.count_comments);  // New TextView for comments count
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
            shimmerLayout = itemView.findViewById(R.id.shimmer_layout);
            postContent = itemView.findViewById(R.id.post_content);
        }
    }


}